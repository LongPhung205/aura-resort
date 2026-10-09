import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AdminLedgerService } from '../../core/services/admin-ledger.service';
import {
  LedgerItem,
  LedgerItemRequest,
  ReconcileRequest,
} from '../../core/models/admin-ledger.model';

@Component({
  selector: 'app-report-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './report-management.component.html',
  styleUrls: ['./report-management.component.scss'],
})
export class ReportManagementComponent implements OnInit {
  transactions: LedgerItem[] = [];
  filteredTransactions: LedgerItem[] = [];
  paginatedTransactions: LedgerItem[] = [];
  loading = true;
  isSaving = false;

  // KPIs
  totalAmount = 0; // Tổng thực thu (SUCCESS)
  pendingAmount = 0; // Tổng chờ thu (PENDING)
  failedAmount = 0; // Tổng thất bại (FAILED)
  successCount = 0;
  pendingCount = 0;
  failedCount = 0;
  reconciledCount = 0;
  eligibleCount = 0;
  unreconciledCount = 0;
  reconciliationRate = 0;

  // Filters
  searchTerm = '';
  selectedMethod = 'ALL';
  selectedType = 'ALL';
  selectedStatusTab = 'ALL'; // 'ALL' | 'SUCCESS' | 'PENDING' | 'FAILED' | 'RECONCILED' | 'UNRECONCILED'
  datePreset = 'ALL'; // 'ALL' | 'TODAY' | 'LAST_7_DAYS' | 'THIS_MONTH' | 'CUSTOM'
  fromDate = '';
  toDate = '';

  // Pagination
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  pagesArray: number[] = [];

  // Batch Selection & Reconcile
  selectedTxIds = new Set<number>();
  selectAll = false;
  showBatchReconcileModal = false;
  batchReconcileNote = 'Đã đối soát khớp tiền tài khoản / két tiền';
  batchReconcileStatus = 'SUCCESS';
  isBatchReconciling = false;

  // Modals
  showReconcileModal = false;
  showManualTxModal = false;
  showDetailModal = false;
  selectedTransaction: LedgerItem | null = null;

  // Reconcile Form (Single)
  reconcileForm: ReconcileRequest = {
    status: 'SUCCESS',
    note: '',
  };

  // Manual Transaction Form
  manualTxForm: LedgerItemRequest = {
    title: '',
    guestName: '',
    amount: 0,
    paymentMethod: 'CASH',
    ledgerType: 'ROOM_CHARGE',
    referenceNo: '',
    notes: '',
  };

  // Toast
  toastMessage: string | null = null;
  toastType: 'success' | 'error' | 'info' = 'success';
  private toastTimeout: any;

  constructor(
    private ledgerService: AdminLedgerService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['method']) {
        const m = params['method'].toUpperCase();
        if (m.includes('PAYOS') || m.includes('VNPAY') || m.includes('BANK')) {
          this.selectedMethod = 'PAYOS';
        } else if (m.includes('MOMO')) {
          this.selectedMethod = 'MOMO';
        } else if (m.includes('CASH')) {
          this.selectedMethod = 'CASH';
        } else {
          this.selectedMethod = m;
        }
      }
      if (params['status']) {
        this.selectedStatusTab = params['status'].toUpperCase();
      }
      this.loadTransactions();
    });
  }

  loadTransactions(): void {
    this.loading = true;
    this.ledgerService.getLedger('ALL', 'ALL').subscribe({
      next: (data) => {
        this.transactions = (data || []).sort((a, b) => {
          const timeA = a.paymentTime ? new Date(a.paymentTime).getTime() : 0;
          const timeB = b.paymentTime ? new Date(b.paymentTime).getTime() : 0;
          return timeB - timeA;
        });
        this.calculateKpis();
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Lỗi khi tải sổ cái:', err);
        this.loading = false;
        this.showToast('Không thể kết nối đến máy chủ lấy sổ cái!', 'error');
      },
    });
  }

  isCancelledOrFailed(tx: LedgerItem | null | undefined): boolean {
    if (!tx) return false;
    return (
      tx.status === 'FAILED' ||
      tx.status === 'CANCELLED' ||
      tx.bookingStatus === 'CANCELLED'
    );
  }

  isEligibleForReconcile(tx: LedgerItem | null | undefined): boolean {
    if (!tx) return false;
    return !this.isCancelledOrFailed(tx);
  }

  calculateKpis(): void {
    const successList = this.transactions.filter((t) => t.status === 'SUCCESS');
    const pendingList = this.transactions.filter(
      (t) => t.status === 'PENDING' && !this.isCancelledOrFailed(t)
    );
    const failedList = this.transactions.filter((t) => this.isCancelledOrFailed(t));
    const eligibleList = this.transactions.filter((t) => this.isEligibleForReconcile(t));
    const reconciledList = eligibleList.filter(
      (t) => !!t.reconciledBy || !!t.reconciliationNote
    );

    this.totalAmount = successList.reduce((acc, t) => acc + (t.amount || 0), 0);
    this.pendingAmount = pendingList.reduce((acc, t) => acc + (t.amount || 0), 0);
    this.failedAmount = failedList.reduce((acc, t) => acc + (t.amount || 0), 0);

    this.successCount = successList.length;
    this.pendingCount = pendingList.length;
    this.failedCount = failedList.length;
    this.eligibleCount = eligibleList.length;
    this.reconciledCount = reconciledList.length;
    this.unreconciledCount = eligibleList.filter(
      (t) => !t.reconciledBy && !t.reconciliationNote
    ).length;

    this.reconciliationRate =
      this.eligibleCount > 0
        ? Math.round((this.reconciledCount / this.eligibleCount) * 100)
        : 100;
  }

  applyFilter(): void {
    const now = new Date();
    const todayStr = now.toISOString().slice(0, 10);

    this.filteredTransactions = this.transactions.filter((t) => {
      // 1. Payment Method
      if (this.selectedMethod !== 'ALL') {
        const method = (t.paymentMethod || 'CASH').toUpperCase();
        if (this.selectedMethod === 'MOMO' && !method.includes('MOMO')) return false;
        if (this.selectedMethod === 'VNPAY' && !method.includes('VNPAY')) return false;
        if (
          this.selectedMethod === 'PAYOS' &&
          !method.includes('PAYOS') &&
          !method.includes('VNPAY') &&
          !method.includes('BANK') &&
          !method.includes('TRANSFER') &&
          !method.includes('STRIPE')
        ) {
          return false;
        }
        if (
          this.selectedMethod === 'BANK_TRANSFER' &&
          !method.includes('BANK') &&
          !method.includes('TRANSFER')
        ) {
          return false;
        }
        if (
          this.selectedMethod === 'CASH' &&
          !method.includes('CASH') &&
          !method.includes('POS') &&
          !method.includes('TIỀN')
        ) {
          return false;
        }
      }

      // 2. Ledger Type
      if (this.selectedType !== 'ALL') {
        if (t.ledgerType !== this.selectedType) return false;
      }

      // 3. Status Tab
      if (this.selectedStatusTab === 'SUCCESS' && t.status !== 'SUCCESS') return false;
      if (
        this.selectedStatusTab === 'PENDING' &&
        (t.status !== 'PENDING' || this.isCancelledOrFailed(t))
      ) {
        return false;
      }
      if (
        this.selectedStatusTab === 'FAILED' &&
        !this.isCancelledOrFailed(t)
      ) {
        return false;
      }
      if (
        this.selectedStatusTab === 'RECONCILED' &&
        (!this.isEligibleForReconcile(t) || (!t.reconciledBy && !t.reconciliationNote))
      ) {
        return false;
      }
      if (
        this.selectedStatusTab === 'UNRECONCILED' &&
        (!this.isEligibleForReconcile(t) || t.reconciledBy || t.reconciliationNote)
      ) {
        return false;
      }

      // 4. Date Filter
      if (this.datePreset !== 'ALL' && t.paymentTime) {
        const txDate = new Date(t.paymentTime);
        const txDateStr = txDate.toISOString().slice(0, 10);

        if (this.datePreset === 'TODAY') {
          if (txDateStr !== todayStr) return false;
        } else if (this.datePreset === 'LAST_7_DAYS') {
          const sevenDaysAgo = new Date();
          sevenDaysAgo.setDate(now.getDate() - 7);
          if (txDate < sevenDaysAgo) return false;
        } else if (this.datePreset === 'THIS_MONTH') {
          if (
            txDate.getMonth() !== now.getMonth() ||
            txDate.getFullYear() !== now.getFullYear()
          ) {
            return false;
          }
        } else if (this.datePreset === 'CUSTOM') {
          if (this.fromDate && txDateStr < this.fromDate) return false;
          if (this.toDate && txDateStr > this.toDate) return false;
        }
      }

      // 5. Search Term
      if (this.searchTerm && this.searchTerm.trim() !== '') {
        const q = this.searchTerm.trim().toLowerCase();
        const searchStr = `${t.transactionId || ''} ${t.bookingCode || ''} ${t.referenceNo || ''} ${t.guestName || ''} ${t.guestPhone || ''} ${t.guestEmail || ''} ${t.roomName || ''}`.toLowerCase();
        if (!searchStr.includes(q)) return false;
      }

      return true;
    });

    this.currentPage = 1;
    this.updatePagination();
  }

  updatePagination(): void {
    this.totalPages = Math.max(
      1,
      Math.ceil(this.filteredTransactions.length / this.pageSize)
    );
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedTransactions = this.filteredTransactions.slice(
      startIndex,
      endIndex
    );

    // Update selectAll status (only consider eligible transactions)
    const eligibleOnPage = this.paginatedTransactions.filter((t) => this.isEligibleForReconcile(t));
    this.selectAll =
      eligibleOnPage.length > 0 &&
      eligibleOnPage.every((t) => this.selectedTxIds.has(t.id));

    // Calculate pages array (max 5 visible buttons)
    const pages: number[] = [];
    let startPage = Math.max(1, this.currentPage - 2);
    let endPage = Math.min(this.totalPages, startPage + 4);
    if (endPage - startPage < 4) {
      startPage = Math.max(1, endPage - 4);
    }
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    this.pagesArray = pages;
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages || page === this.currentPage) return;
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedTransactions = this.filteredTransactions.slice(
      startIndex,
      startIndex + this.pageSize
    );
    const eligibleOnPage = this.paginatedTransactions.filter((t) => this.isEligibleForReconcile(t));
    this.selectAll =
      eligibleOnPage.length > 0 &&
      eligibleOnPage.every((t) => this.selectedTxIds.has(t.id));
  }

  onPageSizeChange(): void {
    this.currentPage = 1;
    this.updatePagination();
  }

  setStatusTab(tab: string): void {
    this.selectedStatusTab = tab;
    this.applyFilter();
  }

  setDatePreset(preset: string): void {
    this.datePreset = preset;
    if (preset !== 'CUSTOM') {
      this.fromDate = '';
      this.toDate = '';
    }
    this.applyFilter();
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.selectedMethod = 'ALL';
    this.selectedType = 'ALL';
    this.selectedStatusTab = 'ALL';
    this.datePreset = 'ALL';
    this.fromDate = '';
    this.toDate = '';
    this.applyFilter();
  }

  isTxReconciled(tx: LedgerItem): boolean {
    return !!(tx.reconciledBy || tx.reconciliationNote);
  }

  // Selection Checkbox Methods
  toggleSelectAll(): void {
    const eligibleOnPage = this.paginatedTransactions.filter((t) => this.isEligibleForReconcile(t));
    if (this.selectAll) {
      eligibleOnPage.forEach((t) => this.selectedTxIds.delete(t.id));
      this.selectAll = false;
    } else {
      eligibleOnPage.forEach((t) => this.selectedTxIds.add(t.id));
      this.selectAll = eligibleOnPage.length > 0;
    }
  }

  toggleSelectTx(id: number, event?: Event): void {
    if (event) event.stopPropagation();
    const tx = this.transactions.find((t) => t.id === id);
    if (tx && !this.isEligibleForReconcile(tx)) {
      this.showToast('Giao dịch đã hủy / thất bại được miễn đối soát!', 'info');
      return;
    }
    if (this.selectedTxIds.has(id)) {
      this.selectedTxIds.delete(id);
      this.selectAll = false;
    } else {
      this.selectedTxIds.add(id);
      const eligibleOnPage = this.paginatedTransactions.filter((t) => this.isEligibleForReconcile(t));
      if (eligibleOnPage.length > 0 && eligibleOnPage.every((t) => this.selectedTxIds.has(t.id))) {
        this.selectAll = true;
      }
    }
  }

  isTxSelected(id: number): boolean {
    return this.selectedTxIds.has(id);
  }

  // Batch Reconcile Modal
  openBatchReconcileModal(): void {
    // Exclude any ineligible items that might be in selection
    for (const id of Array.from(this.selectedTxIds)) {
      const tx = this.transactions.find((t) => t.id === id);
      if (tx && !this.isEligibleForReconcile(tx)) {
        this.selectedTxIds.delete(id);
      }
    }

    if (this.selectedTxIds.size === 0) {
      // Auto-select all eligible unreconciled in filtered list
      const unreconciled = this.filteredTransactions.filter(
        (t) => this.isEligibleForReconcile(t) && !t.reconciledBy && !t.reconciliationNote
      );
      if (unreconciled.length === 0) {
        this.showToast(
          'Tất cả giao dịch hợp lệ hiển thị đã được đối soát hoàn tất (đơn hủy được miễn đối soát)!',
          'info'
        );
        return;
      }
      unreconciled.forEach((t) => this.selectedTxIds.add(t.id));
    }
    this.batchReconcileNote = 'Đã đối soát khớp sao kê ngân hàng & tiền két';
    this.batchReconcileStatus = 'SUCCESS';
    this.showBatchReconcileModal = true;
  }

  closeBatchReconcileModal(): void {
    this.showBatchReconcileModal = false;
    this.isBatchReconciling = false;
  }

  executeBatchReconcile(): void {
    if (this.selectedTxIds.size === 0) return;
    this.isBatchReconciling = true;

    const ids = Array.from(this.selectedTxIds);
    let successCount = 0;
    let completed = 0;

    ids.forEach((id) => {
      this.ledgerService
        .reconcileTransaction(id, {
          status: this.batchReconcileStatus,
          note: this.batchReconcileNote,
        })
        .subscribe({
          next: (updated) => {
            const idx = this.transactions.findIndex((t) => t.id === updated.id);
            if (idx > -1) {
              this.transactions[idx] = updated;
            }
            successCount++;
            completed++;
            if (completed === ids.length) {
              this.finishBatchReconcile(successCount);
            }
          },
          error: () => {
            completed++;
            if (completed === ids.length) {
              this.finishBatchReconcile(successCount);
            }
          },
        });
    });
  }

  private finishBatchReconcile(count: number): void {
    this.calculateKpis();
    this.applyFilter();
    this.selectedTxIds.clear();
    this.selectAll = false;
    this.isBatchReconciling = false;
    this.closeBatchReconcileModal();
    this.showToast(
      `Đã đối soát thành công ${count} giao dịch vào sổ cái!`,
      'success'
    );
  }

  // Quick One-Click Reconcile from Table Row
  quickReconcileOne(tx: LedgerItem, event?: MouseEvent): void {
    if (event) event.stopPropagation();
    if (!this.isEligibleForReconcile(tx)) {
      this.showToast('Đơn hàng/giao dịch đã hủy không cần đối soát!', 'info');
      return;
    }
    const defaultNote = tx.reconciledBy ? 'Xác nhận lại khớp tiền' : 'Đã khớp sao kê tài khoản ngân hàng';
    
    this.ledgerService
      .reconcileTransaction(tx.id, {
        status: tx.status || 'SUCCESS',
        note: defaultNote,
      })
      .subscribe({
        next: (updated) => {
          const idx = this.transactions.findIndex((t) => t.id === updated.id);
          if (idx > -1) this.transactions[idx] = updated;
          if (this.selectedTransaction && this.selectedTransaction.id === updated.id) {
            this.selectedTransaction = updated;
          }
          this.calculateKpis();
          this.applyFilter();
          this.showToast(
            `Đã đối soát khớp giao dịch #${updated.transactionId || updated.id}!`,
            'success'
          );
        },
        error: () => {
          this.showToast('Lỗi khi thực hiện đối soát!', 'error');
        },
      });
  }

  // Details Modal
  openDetailModal(tx: LedgerItem): void {
    this.selectedTransaction = tx;
    this.showDetailModal = true;
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
  }

  printReceipt(): void {
    window.print();
  }

  // Reconcile Modal (Single)
  openReconcileModal(tx: LedgerItem, event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }
    if (!this.isEligibleForReconcile(tx)) {
      this.showToast('Đơn hàng/giao dịch đã hủy được miễn đối soát!', 'info');
      return;
    }
    this.selectedTransaction = tx;
    this.reconcileForm = {
      status: tx.status || 'SUCCESS',
      note: tx.reconciliationNote || '',
    };
    this.showReconcileModal = true;
  }

  closeReconcileModal(): void {
    this.showReconcileModal = false;
    this.isSaving = false;
  }

  setQuickNote(note: string): void {
    if (!this.reconcileForm.note) {
      this.reconcileForm.note = note;
    } else {
      this.reconcileForm.note += ` - ${note}`;
    }
  }

  saveReconcile(): void {
    if (!this.selectedTransaction) return;
    this.isSaving = true;

    this.ledgerService
      .reconcileTransaction(this.selectedTransaction.id, this.reconcileForm)
      .subscribe({
        next: (updatedTx) => {
          const index = this.transactions.findIndex((t) => t.id === updatedTx.id);
          if (index > -1) {
            this.transactions[index] = updatedTx;
          }
          if (
            this.selectedTransaction &&
            this.selectedTransaction.id === updatedTx.id
          ) {
            this.selectedTransaction = updatedTx;
          }
          this.calculateKpis();
          this.applyFilter();
          this.isSaving = false;
          this.closeReconcileModal();
          this.showToast(
            `Đối soát giao dịch #${updatedTx.transactionId || updatedTx.id} thành công!`,
            'success'
          );
        },
        error: (err) => {
          console.error(err);
          this.isSaving = false;
          this.showToast(
            'Không thể lưu đối soát. Vui lòng kiểm tra lại quyền truy cập.',
            'error'
          );
        },
      });
  }

  // Manual Transaction Modal
  openManualTxModal(): void {
    this.manualTxForm = {
      title: '',
      guestName: '',
      amount: 0,
      paymentMethod: 'CASH',
      ledgerType: 'ROOM_CHARGE',
      referenceNo: `PT-${Date.now().toString().slice(-6)}`,
      notes: '',
    };
    this.showManualTxModal = true;
  }

  closeManualTxModal(): void {
    this.showManualTxModal = false;
    this.isSaving = false;
  }

  saveManualTx(): void {
    if (!this.manualTxForm.amount || this.manualTxForm.amount <= 0) {
      this.showToast('Vui lòng nhập số tiền hợp lệ lớn hơn 0!', 'error');
      return;
    }
    if (
      !this.manualTxForm.guestName ||
      this.manualTxForm.guestName.trim() === ''
    ) {
      this.manualTxForm.guestName = 'Khách vãng lai';
    }

    this.isSaving = true;
    this.ledgerService.createTransaction(this.manualTxForm).subscribe({
      next: (newTx) => {
        this.transactions.unshift(newTx);
        this.calculateKpis();
        this.applyFilter();
        this.isSaving = false;
        this.closeManualTxModal();
        this.showToast(
          `Lập phiếu #${newTx.transactionId || newTx.referenceNo} thành công!`,
          'success'
        );
      },
      error: (err) => {
        console.error(err);
        this.isSaving = false;
        this.showToast('Lỗi khi lập phiếu thu chi thủ công!', 'error');
      },
    });
  }

  // Export CSV
  exportCsv(): void {
    if (!this.filteredTransactions || this.filteredTransactions.length === 0) {
      this.showToast('Không có dữ liệu giao dịch để xuất báo cáo!', 'info');
      return;
    }

    const headers = [
      'Mã Giao Dịch',
      'Mã Đơn Hàng',
      'Mã Tham Chiếu',
      'Khách Hàng',
      'Số Điện Thoại',
      'Phòng / Căn',
      'Loại Bút Toán',
      'Phương Thức',
      'Số Tiền (VNĐ)',
      'Trạng Thái',
      'Thời Gian Giao Dịch',
      'Trạng Thái Đối Soát',
      'Người Đối Soát',
      'Thời Gian Đối Soát',
      'Ghi Chú Đối Soát',
    ];

    const rows = this.filteredTransactions.map((t) => [
      `"${t.transactionId || ''}"`,
      `"${t.bookingCode || ''}"`,
      `"${t.referenceNo || ''}"`,
      `"${t.guestName || ''}"`,
      `"${t.guestPhone || ''}"`,
      `"${t.roomName || ''}"`,
      `"${this.getLedgerTypeLabel(t.ledgerType)}"`,
      `"${t.paymentMethod || 'CASH'}"`,
      t.amount || 0,
      `"${this.getStatusLabel(t.status, t.bookingStatus)}"`,
      `"${t.paymentTime ? new Date(t.paymentTime).toLocaleString('vi-VN') : ''}"`,
      `"${this.isCancelledOrFailed(t) ? 'Miễn đối soát (Đã hủy)' : (t.reconciledBy ? 'Đã đối soát' : 'Chưa đối soát')}"`,
      `"${t.reconciledBy || ''}"`,
      `"${t.reconciliationTime ? new Date(t.reconciliationTime).toLocaleString('vi-VN') : ''}"`,
      `"${(t.reconciliationNote || '').replace(/"/g, '""')}"`,
    ]);

    const csvContent =
      '\uFEFF' +
      [headers.join(','), ...rows.map((e) => e.join(','))].join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute(
      'download',
      `So_Quy_Doi_Soat_${new Date().toISOString().slice(0, 10)}.csv`
    );
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    this.showToast('Xuất báo cáo CSV thành công!', 'success');
  }

  // Toast helper
  showToast(
    message: string,
    type: 'success' | 'error' | 'info' = 'success'
  ): void {
    this.toastMessage = message;
    this.toastType = type;
    if (this.toastTimeout) {
      clearTimeout(this.toastTimeout);
    }
    this.toastTimeout = setTimeout(() => {
      this.toastMessage = null;
    }, 4000);
  }

  // UI Helpers
  getStatusLabel(status: string | undefined, bookingStatus?: string): string {
    if (status === 'CANCELLED' || bookingStatus === 'CANCELLED') return 'Đã hủy';
    if (status === 'SUCCESS') return 'Thành công';
    if (status === 'PENDING') return 'Chờ thu / COD';
    return 'Thất bại';
  }

  getBadgeColor(method: string | undefined): string {
    if (!method) return 'bg-slate-100 text-slate-700 border-slate-200';
    const m = method.toUpperCase();
    if (m.includes('MOMO')) return 'bg-pink-50 text-pink-700 border-pink-200';
    if (m.includes('VNPAY')) return 'bg-sky-50 text-sky-700 border-sky-200';
    if (m.includes('PAYOS')) return 'bg-indigo-50 text-indigo-700 border-indigo-200';
    if (m.includes('CASH')) return 'bg-amber-50 text-amber-800 border-amber-200';
    if (m.includes('BANK')) return 'bg-blue-50 text-blue-700 border-blue-200';
    return 'bg-purple-50 text-purple-700 border-purple-200';
  }

  getMethodIcon(method: string | undefined): string {
    if (!method) return 'payments';
    const m = method.toUpperCase();
    if (m.includes('MOMO')) return 'account_balance_wallet';
    if (m.includes('VNPAY')) return 'credit_card';
    if (m.includes('PAYOS')) return 'qr_code_2';
    if (m.includes('CASH')) return 'payments';
    if (m.includes('BANK')) return 'account_balance';
    return 'point_of_sale';
  }

  getStatusIcon(status: string): string {
    if (status === 'SUCCESS') return 'check_circle';
    if (status === 'PENDING') return 'hourglass_empty';
    if (status === 'CANCELLED') return 'cancel';
    return 'cancel';
  }

  getStatusColor(status: string): string {
    if (status === 'SUCCESS')
      return 'text-emerald-600 bg-emerald-50 border-emerald-200';
    if (status === 'PENDING')
      return 'text-amber-700 bg-amber-50 border-amber-200';
    return 'text-rose-600 bg-rose-50 border-rose-200';
  }

  getLedgerTypeLabel(type: string | undefined): string {
    if (!type) return 'Tiền phòng';
    switch (type.toUpperCase()) {
      case 'ROOM_CHARGE':
        return 'Tiền phòng';
      case 'EXTRA_SERVICE':
        return 'Dịch vụ phát sinh';
      case 'DEPOSIT':
        return 'Tiền cọc';
      case 'OPEX':
        return 'Chi phí vận hành';
      case 'REFUND':
        return 'Hoàn tiền';
      default:
        return type;
    }
  }

  getLedgerTypeBadge(type: string | undefined): string {
    if (!type) return 'bg-slate-100 text-slate-700';
    switch (type.toUpperCase()) {
      case 'ROOM_CHARGE':
        return 'bg-blue-50 text-blue-700 border-blue-200';
      case 'EXTRA_SERVICE':
        return 'bg-purple-50 text-purple-700 border-purple-200';
      case 'DEPOSIT':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'OPEX':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      case 'REFUND':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  }

  copyToClipboard(text: string, label: string): void {
    if (!text) return;
    navigator.clipboard.writeText(text).then(() => {
      this.showToast(`Đã sao chép ${label}: ${text}`, 'info');
    });
  }

  getShowingFrom(): number {
    if (this.filteredTransactions.length === 0) return 0;
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  getShowingTo(): number {
    return Math.min(
      this.currentPage * this.pageSize,
      this.filteredTransactions.length
    );
  }
}
