/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { ComboPackageService } from '../../core/services/combo-package.service';
import { AdminExtraServiceService, ExtraServiceItem } from '../../core/services/admin-extra-service.service';
import { PromotionService } from '../../core/services/promotion.service';

@Component({
  selector: 'app-promotion-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './promotion-management.component.html',
  styleUrls: ['./promotion-management.component.scss'],
})
export class PromotionManagementComponent implements OnInit {
  activeTab = 'VOUCHER';
  toastMessage: string | null = null;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private comboService: ComboPackageService,
    private extraServiceSvc: AdminExtraServiceService,
    private promotionService: PromotionService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['tab']) {
        const t = params['tab'].toUpperCase();
        if (t === 'VOUCHER' || t === 'COMBO' || t === 'PRICING') {
          this.activeTab = t;
        }
      }
    });

    this.loadExtraServices();
    this.loadCombos();
    this.loadVouchers();
  }

  availableExtraServices: ExtraServiceItem[] = [];

  loadExtraServices() {
    this.extraServiceSvc.getAll().subscribe({
      next: (data) => {
        this.availableExtraServices = data;
        this.cdr.detectChanges();
      }
    });
  }

  // 1. Dynamic Pricing Form
  showPricingForm = false;
  pricingForm: any = {
    id: null,
    campaignName: '',
    ruleType: 'DATES', // 'DATES' (Ngày lễ/tết) hoặc 'WEEKENDS' (Thứ 7, CN)
    startDate: '',
    endDate: '',
    adjustmentType: 'INCREASE',
    unit: 'PERCENT',
    value: null as number | null,
    applicableVillas: [] as string[]
  };
  villaOptions = ['Phân Khu Ngọc Trai', 'Phân Khu Sao Biển', 'Phân Khu San Hô', 'Khu Nghỉ Dưỡng Đặc Biệt'];

  isEditingPricing = false;
  pricingList: any[] = [
    { id: 1, campaignName: 'Tết Nguyên Đán 2027', ruleType: 'DATES', startDate: '2027-02-05', endDate: '2027-02-15', adjustmentType: 'INCREASE', unit: 'PERCENT', value: 30, applicableVillas: ['Phân Khu Ngọc Trai'] },
    { id: 2, campaignName: 'Phụ thu cuối tuần', ruleType: 'WEEKENDS', startDate: '', endDate: '', adjustmentType: 'INCREASE', unit: 'PERCENT', value: 10, applicableVillas: ['Phân Khu Sao Biển', 'Khu Nghỉ Dưỡng Đặc Biệt'] }
  ];

  // 2. Combo Package Form
  showComboForm = false;
  isEditingCombo = false;
  isUploadingComboImage = false;
  comboLoading = false;

  comboList: any[] = [];
  comboForm: any = {
    id: null,
    name: '',
    description: '',
    extraServiceIds: [] as number[],
    price: null as number | null,
    status: 'PUBLISH',
    image: null as string | null
  };

  // 3. Voucher Generator Form
  showVoucherForm = false;
  isEditingVoucher = false;
  voucherLoading = false;
  vouchersList: any[] = [];
  voucherForm: any = {
    id: null,
    code: '',
    name: '',
    discountType: 'PERCENTAGE',
    discountValue: null as number | null,
    quantity: 100,
    totalLimit: 100,
    startDate: '',
    endDate: '',
    category: 'SUMMER'
  };

  setTab(tab: 'PRICING' | 'COMBO' | 'VOUCHER') {
    this.activeTab = tab;
  }

  toggleVilla(villa: string) {
    const idx = this.pricingForm.applicableVillas.indexOf(villa);
    if (idx > -1) {
      this.pricingForm.applicableVillas.splice(idx, 1);
    } else {
      this.pricingForm.applicableVillas.push(villa);
    }
  }

  toggleExtraService(serviceId: number) {
    const idx = this.comboForm.extraServiceIds.indexOf(serviceId);
    if (idx > -1) {
      this.comboForm.extraServiceIds.splice(idx, 1);
    } else {
      this.comboForm.extraServiceIds.push(serviceId);
    }
  }

  editPricing(p: any) {
    this.isEditingPricing = true;
    this.showPricingForm = true;
    this.pricingForm = { ...p, applicableVillas: [...p.applicableVillas] };
  }

  deletePricing(p: any) {
    if(confirm('Bạn có chắc chắn muốn xóa quy tắc ' + p.campaignName + '?')) {
      this.pricingList = this.pricingList.filter(item => item.id !== p.id);
      this.showToast('Đã xóa quy tắc: ' + p.campaignName);
    }
  }

  cancelEditPricing() {
    this.isEditingPricing = false;
    this.showPricingForm = false;
    this.pricingForm = {
      id: null,
      campaignName: '',
      ruleType: 'DATES',
      startDate: '',
      endDate: '',
      adjustmentType: 'INCREASE',
      unit: 'PERCENT',
      value: null,
      applicableVillas: []
    };
  }

  savePricing() {
    if (!this.pricingForm.campaignName) return this.showToast('Vui lòng nhập tên chiến dịch!');
    
    if (this.isEditingPricing) {
      const idx = this.pricingList.findIndex(x => x.id === this.pricingForm.id);
      if (idx !== -1) {
        this.pricingList[idx] = { ...this.pricingForm, applicableVillas: [...this.pricingForm.applicableVillas] };
      }
      this.showToast('Đã cập nhật Quy tắc Giá thành công!');
    } else {
      const newRule = {
        ...this.pricingForm,
        id: Date.now(),
        applicableVillas: [...this.pricingForm.applicableVillas]
      };
      this.pricingList.unshift(newRule);
      this.showToast('Đã lưu Quy tắc Giá động thành công!');
    }
    
    this.cancelEditPricing();
  }

  // --- COMBO PACKAGE MANAGEMENT ---
  loadCombos(): void {
    this.comboLoading = true;
    this.comboService.getAll().subscribe({
      next: (data) => {
        this.comboList = data.map(c => ({
          ...c,
          image: c.imageUrl,
          extraServices: c.extraServices || []
        }));
        this.comboLoading = false;
        this.cdr.detectChanges();
      },
      error: () => { this.comboLoading = false; }
    });
  }

  editCombo(c: any) {
    this.isEditingCombo = true;
    this.showComboForm = true;
    this.comboForm = { 
      ...c,
      extraServiceIds: c.extraServices ? c.extraServices.map((s: any) => s.id) : [],
      image: c.image || null
    };
  }

  deleteCombo(c: any) {
    if (!c.id) return;
    if (confirm('Bạn có chắc chắn muốn xóa Combo ' + c.name + '?')) {
      this.comboService.delete(c.id).subscribe({
        next: () => {
          this.comboList = this.comboList.filter(item => item.id !== c.id);
          this.showToast('Đã xóa Combo: ' + c.name);
          this.cdr.detectChanges();
        },
        error: () => this.showToast('Lỗi khi xóa combo!')
      });
    }
  }

  cancelEditCombo() {
    this.isEditingCombo = false;
    this.showComboForm = false;
    this.isUploadingComboImage = false;
    this.comboForm = {
      id: null,
      name: '',
      description: '',
      extraServiceIds: [],
      price: null,
      status: 'PUBLISH',
      image: null
    };
  }

  onComboImageUpload(event: any) {
    const file = event.target.files?.[0];
    if (!file) return;

    // Reset file input value to allow picking same file again
    event.target.value = '';

    // 1. Immediate local preview
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.comboForm.image = e.target.result;
      this.cdr.detectChanges();
    };
    reader.readAsDataURL(file);

    // 2. Upload to server
    this.isUploadingComboImage = true;
    const formData = new FormData();
    formData.append('file', file);
    this.http.post<any>('http://localhost:8080/api/v1/villas/upload-image', formData).subscribe({
      next: (res) => {
        this.isUploadingComboImage = false;
        if (res && res.data) {
          // Backend trả về "/assets/images/uploads/villa_xxx.jpg"
          // ng serve không serve được file mới thêm sau khi đã khởi động.
          // Chuyển sang URL backend trực tiếp: /api/v1/villas/images/{filename}
          const serverPath: string = res.data;
          const prefix = '/assets/images/uploads/';
          if (serverPath.startsWith(prefix)) {
            const filename = serverPath.substring(prefix.length);
            this.comboForm.image = `http://localhost:8080/api/v1/villas/images/${filename}`;
          } else {
            this.comboForm.image = serverPath;
          }
        }
        this.showToast('Tải ảnh combo lên máy chủ thành công!');
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.warn('Không thể tải ảnh lên máy chủ, lưu bản xem trước cục bộ', err);
        this.isUploadingComboImage = false;
        this.showToast('Đã chọn ảnh combo thành công!');
        this.cdr.detectChanges();
      }
    });
  }

  removeComboImage(event?: Event) {
    if (event) {
      event.stopPropagation();
    }
    this.comboForm.image = null;
  }

  saveCombo() {
    if (!this.comboForm.name) return this.showToast('Vui lòng nhập tên gói Combo!');

    const payload = {
      name: this.comboForm.name,
      description: this.comboForm.description,
      imageUrl: this.comboForm.image,
      price: this.comboForm.price,
      status: this.comboForm.status || 'PUBLISH',
      extraServiceIds: this.comboForm.extraServiceIds,
    };

    if (this.isEditingCombo && this.comboForm.id) {
      this.comboService.update(this.comboForm.id, payload as any).subscribe({
        next: () => {
          this.showToast('Đã cập nhật Combo thành công!');
          this.cancelEditCombo();
          this.loadCombos();
        },
        error: () => this.showToast('Lỗi khi cập nhật combo!')
      });
    } else {
      this.comboService.create(payload as any).subscribe({
        next: () => {
          this.showToast('Đã xuất bản Combo mới thành công!');
          this.cancelEditCombo();
          this.loadCombos();
        },
        error: () => this.showToast('Lỗi khi tạo combo!')
      });
    }
  }

  // --- VOUCHER MANAGEMENT ---
  loadVouchers() {
    this.voucherLoading = true;
    this.promotionService.getAllPromotions().subscribe({
      next: (data) => {
        let list = data || [];
        if (list.length === 0) {
          // If empty, also attempt public active endpoint
          this.promotionService.getActivePromotions().subscribe({
            next: (activeData) => {
              this.applyVoucherList(activeData || []);
            },
            error: () => {
              this.applyVoucherList([]);
            }
          });
          return;
        }
        this.applyVoucherList(list);
      },
      error: (err) => {
        console.warn('Could not load promotions from admin endpoint, fallback to active', err);
        this.promotionService.getActivePromotions().subscribe({
          next: (activeData) => {
            this.applyVoucherList(activeData || []);
          },
          error: () => {
            this.applyVoucherList([]);
          }
        });
      }
    });
  }

  private applyVoucherList(list: any[]) {
    const now = new Date();
    this.vouchersList = list.map((item: any) => {
      let status = 'ACTIVE';
      if (item.quantity !== undefined && item.quantity !== null && item.quantity <= 0) {
        status = 'EXHAUSTED';
      } else if (item.endDate) {
        const endD = new Date(item.endDate);
        endD.setHours(23, 59, 59, 999);
        if (endD < now) {
          status = 'EXPIRED';
        }
      }
      return {
        ...item,
        status: status
      };
    });
    this.voucherLoading = false;
    this.cdr.detectChanges();
  }

  openCreateVoucher() {
    this.isEditingVoucher = false;
    this.showVoucherForm = true;
    const today = new Date();
    const nextYear = new Date();
    nextYear.setFullYear(today.getFullYear() + 1);
    this.voucherForm = {
      id: null,
      code: '',
      name: '',
      discountType: 'PERCENTAGE',
      discountValue: null,
      quantity: 100,
      totalLimit: 100,
      startDate: today.toISOString().split('T')[0],
      endDate: nextYear.toISOString().split('T')[0],
      category: 'SUMMER'
    };
  }

  generateVoucherCode() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = 'FLC';
    for (let i = 0; i < 4; i++) {
      code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    this.voucherForm.code = code;
  }

  editVoucher(v: any) {
    this.isEditingVoucher = true;
    this.showVoucherForm = true;
    this.voucherForm = {
      id: v.id,
      code: v.code,
      name: v.name || '',
      discountType: v.discountType === 'AMOUNT' ? 'FIXED_AMOUNT' : (v.discountType === 'PERCENT' ? 'PERCENTAGE' : (v.discountType || 'PERCENTAGE')),
      discountValue: v.discountValue,
      quantity: v.quantity !== undefined && v.quantity !== null ? v.quantity : 100,
      totalLimit: v.quantity !== undefined && v.quantity !== null ? v.quantity : 100,
      startDate: v.startDate,
      endDate: v.endDate,
      category: v.category || 'SUMMER'
    };
  }

  deleteVoucher(v: any) {
    if (confirm('Bạn có chắc chắn muốn xóa voucher ' + v.code + '?')) {
      this.promotionService.deletePromotion(v.id).subscribe({
        next: () => {
          this.showToast('Đã xóa voucher: ' + v.code);
          this.loadVouchers();
        },
        error: (err) => {
          const msg = err.error?.message || 'Lỗi khi xóa voucher!';
          this.showToast(msg);
        }
      });
    }
  }

  cancelEditVoucher() {
    this.isEditingVoucher = false;
    this.showVoucherForm = false;
    this.voucherForm = {
      id: null,
      code: '',
      name: '',
      discountType: 'PERCENTAGE',
      discountValue: null,
      quantity: 100,
      totalLimit: 100,
      startDate: '',
      endDate: '',
      category: 'SUMMER'
    };
  }

  saveVoucher() {
    const code = (this.voucherForm.code || '').trim().toUpperCase();
    if (!code) return this.showToast('Vui lòng tạo hoặc nhập mã Voucher!');
    if (!this.voucherForm.discountValue || this.voucherForm.discountValue <= 0) {
      return this.showToast('Vui lòng nhập mức giảm hợp lệ!');
    }
    if (!this.voucherForm.startDate || !this.voucherForm.endDate) {
      return this.showToast('Vui lòng chọn thời gian bắt đầu và kết thúc!');
    }

    const payload = {
      code: code,
      name: this.voucherForm.name || `Khuyến Mãi ${code}`,
      discountType: this.voucherForm.discountType === 'AMOUNT' ? 'FIXED_AMOUNT' : (this.voucherForm.discountType === 'PERCENT' ? 'PERCENTAGE' : this.voucherForm.discountType),
      discountValue: this.voucherForm.discountValue,
      startDate: this.voucherForm.startDate,
      endDate: this.voucherForm.endDate,
      quantity: this.voucherForm.quantity || this.voucherForm.totalLimit || 100,
      category: this.voucherForm.category || 'SUMMER'
    };

    if (this.isEditingVoucher && this.voucherForm.id) {
      this.promotionService.updatePromotion(this.voucherForm.id, payload).subscribe({
        next: () => {
          this.showToast('Đã cập nhật Voucher ' + code + ' thành công!');
          this.cancelEditVoucher();
          this.loadVouchers();
        },
        error: (err) => {
          const msg = err.error?.message || 'Lỗi khi cập nhật voucher!';
          this.showToast(msg);
        }
      });
    } else {
      this.promotionService.createPromotion(payload).subscribe({
        next: () => {
          this.showToast('Phát hành Voucher ' + code + ' thành công!');
          this.cancelEditVoucher();
          this.loadVouchers();
        },
        error: (err) => {
          const msg = err.error?.message || 'Lỗi khi tạo voucher!';
          this.showToast(msg);
        }
      });
    }
  }

  showToast(msg: string) {
    this.toastMessage = msg;
    setTimeout(() => this.toastMessage = null, 3000);
  }
}
