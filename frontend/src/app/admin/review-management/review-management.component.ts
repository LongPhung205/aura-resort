import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminReviewService } from '../../core/services/admin-review.service';
import { ReviewAnalytics, ReviewItem } from '../../core/models/admin-review.model';

export type FilterTab = 'ALL' | 'NEEDS_REPLY' | 'REPLIED' | '5STAR' | '4STAR' | 'LOW_RATING';
export type SortOption = 'NEWEST' | 'OLDEST' | 'HIGHEST' | 'LOWEST';

@Component({
  selector: 'app-review-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './review-management.component.html',
  styleUrls: ['./review-management.component.scss'],
})
export class ReviewManagementComponent implements OnInit {
  // State
  isLoading = false;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' | 'info' = 'success';

  // Filters & Sorting
  activeFilterTab: FilterTab = 'ALL';
  searchTerm = '';
  sortBy: SortOption = 'NEWEST';

  // Data
  allReviews: ReviewItem[] = [];
  filteredReviews: ReviewItem[] = [];

  // Metrics
  csatScore = 0;
  npsScore = 0;
  totalReviews = 0;
  positiveCount = 0; // 5 stars
  neutralCount = 0;  // 4 stars
  negativeCount = 0; // 1-3 stars
  responseRate = 0;  // %
  needsReplyCount = 0;
  repliedCount = 0;

  // Reply Modal
  showReplyModal = false;
  selectedReviewForReply: ReviewItem | null = null;
  replyContent = '';
  isSubmittingReply = false;

  // Delete Modal
  showDeleteModal = false;
  reviewToDelete: ReviewItem | null = null;
  isDeletingReview = false;

  // Quick Reply Templates
  quickTemplates = [
    {
      title: 'Tri ân trải nghiệm xuất sắc (5 sao)',
      text: 'Chân thành cảm ơn Quý khách đã tin tưởng và lựa chọn kỳ nghỉ tại Aura Luxury Resort. Chúng tôi rất vinh hạnh khi mang đến cho Quý khách và gia đình những khoảnh khắc tuyệt vời. Ban Quản Lý và đội ngũ Quản gia riêng rất mong sớm được chào đón Quý khách trở lại!'
    },
    {
      title: 'Cảm ơn và trân trọng đóng góp (4 sao)',
      text: 'Aura Luxury Resort xin gửi lời cảm ơn chân thành đến Quý khách vì đã dành thời gian chia sẻ cảm nhận quý báu. Những đóng góp của Quý khách là động lực to lớn để đội ngũ tiếp tục hoàn thiện, hướng tới tiêu chuẩn dịch vụ hoàn mỹ nhất. Kính chúc Quý khách luôn nhiều sức khỏe và niềm vui!'
    },
    {
      title: 'Tạ lỗi chân thành & Cam kết khắc phục (< 4 sao)',
      text: 'Ban Quản Lý Aura Resort xin gửi lời xin lỗi chân thành nhất đến Quý khách vì trải nghiệm chưa thực sự trọn vẹn trong kỳ nghỉ vừa qua. Chúng tôi đã nghiêm túc tiếp thu ý kiến và trực tiếp làm việc với các bộ phận để chấn chỉnh quy trình ngay lập tức. Chúng tôi rất mong có cơ hội được đón tiếp và phục vụ Quý khách chu đáo hơn trong tương lai.'
    }
  ];

  constructor(private reviewService: AdminReviewService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    this.isLoading = true;
    this.reviewService.getAnalytics().subscribe({
      next: (analytics: ReviewAnalytics) => {
        this.isLoading = false;
        if (analytics) {
          this.csatScore = analytics.csatScore || 0;
          this.npsScore = analytics.npsScore || 0;
          this.totalReviews = analytics.totalReviews || 0;
          this.positiveCount = analytics.positiveCount || 0;
          this.neutralCount = analytics.neutralCount || 0;
          this.negativeCount = analytics.negativeCount || 0;
          this.responseRate = analytics.responseRate || 0;

          this.allReviews = analytics.latestReviews || [];
          this.calculateCounts();
          this.applyFilters();
        } else {
          this.resetData();
        }
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading reviews:', err);
        this.showToast('Không thể kết nối đến máy chủ lấy dữ liệu đánh giá', 'error');
        this.resetData();
      }
    });
  }

  private resetData(): void {
    this.allReviews = [];
    this.filteredReviews = [];
    this.totalReviews = 0;
    this.needsReplyCount = 0;
    this.repliedCount = 0;
    this.csatScore = 0;
    this.npsScore = 0;
  }

  private calculateCounts(): void {
    this.needsReplyCount = this.allReviews.filter(r => !r.managementReply || r.managementReply.trim() === '').length;
    this.repliedCount = this.allReviews.filter(r => !!r.managementReply && r.managementReply.trim() !== '').length;

    if (this.totalReviews > 0) {
      this.responseRate = Math.round((this.repliedCount / this.totalReviews) * 100);
    } else {
      this.responseRate = 0;
    }
  }

  setTab(tab: FilterTab): void {
    this.activeFilterTab = tab;
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onSortChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let result = [...this.allReviews];

    // 1. Filter by Tab
    switch (this.activeFilterTab) {
      case 'NEEDS_REPLY':
        result = result.filter(r => !r.managementReply || r.managementReply.trim() === '');
        break;
      case 'REPLIED':
        result = result.filter(r => !!r.managementReply && r.managementReply.trim() !== '');
        break;
      case '5STAR':
        result = result.filter(r => r.rating === 5);
        break;
      case '4STAR':
        result = result.filter(r => r.rating === 4);
        break;
      case 'LOW_RATING':
        result = result.filter(r => r.rating && r.rating < 4);
        break;
      case 'ALL':
      default:
        break;
    }

    // 2. Filter by Search Query
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const q = this.searchTerm.trim().toLowerCase();
      result = result.filter(r => {
        const guest = (r.userName || r.guestName || '').toLowerCase();
        const email = (r.userEmail || '').toLowerCase();
        const villa = (r.villaName || r.roomTypeName || '').toLowerCase();
        const code = (r.bookingCode || '').toLowerCase();
        const comment = (r.comment || '').toLowerCase();
        const reply = (r.managementReply || '').toLowerCase();
        return (
          guest.includes(q) ||
          email.includes(q) ||
          villa.includes(q) ||
          code.includes(q) ||
          comment.includes(q) ||
          reply.includes(q)
        );
      });
    }

    // 3. Sort
    result.sort((a, b) => {
      const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
      const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
      const ratingA = a.rating || 0;
      const ratingB = b.rating || 0;

      switch (this.sortBy) {
        case 'OLDEST':
          return dateA - dateB;
        case 'HIGHEST':
          return ratingB - ratingA || dateB - dateA;
        case 'LOWEST':
          return ratingA - ratingB || dateB - dateA;
        case 'NEWEST':
        default:
          return dateB - dateA;
      }
    });

    this.filteredReviews = result;
  }

  // Reply Modal Handlers
  openReplyModal(review: ReviewItem): void {
    this.selectedReviewForReply = review;
    this.replyContent = review.managementReply || '';
    this.showReplyModal = true;
  }

  closeReplyModal(): void {
    this.showReplyModal = false;
    this.selectedReviewForReply = null;
    this.replyContent = '';
    this.isSubmittingReply = false;
  }

  selectTemplate(templateText: string): void {
    this.replyContent = templateText;
  }

  submitReply(): void {
    if (!this.selectedReviewForReply || !this.replyContent.trim()) {
      this.showToast('Vui lòng nhập nội dung phản hồi', 'error');
      return;
    }

    const reviewId = this.selectedReviewForReply.id;
    this.isSubmittingReply = true;

    this.reviewService.replyReview(reviewId, this.replyContent.trim()).subscribe({
      next: (updatedReview) => {
        this.isSubmittingReply = false;
        // Update review in local state
        const target = this.allReviews.find(r => r.id === reviewId);
        if (target) {
          target.managementReply = this.replyContent.trim();
          target.repliedAt = new Date().toISOString();
        }
        this.calculateCounts();
        this.applyFilters();
        this.closeReplyModal();
        this.showToast('Đã đăng phản hồi của Ban Quản Lý thành công!', 'success');
      },
      error: (err) => {
        this.isSubmittingReply = false;
        console.error('Failed to post reply:', err);
        this.showToast('Không thể gửi phản hồi. Vui lòng thử lại!', 'error');
      }
    });
  }

  // Delete Modal Handlers
  openDeleteModal(review: ReviewItem): void {
    this.reviewToDelete = review;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.reviewToDelete = null;
    this.isDeletingReview = false;
  }

  executeDeleteReview(): void {
    if (!this.reviewToDelete) return;

    const id = this.reviewToDelete.id;
    this.isDeletingReview = true;

    this.reviewService.deleteReview(id).subscribe({
      next: (success) => {
        this.isDeletingReview = false;
        if (success) {
          this.allReviews = this.allReviews.filter(r => r.id !== id);
          this.totalReviews = Math.max(0, this.totalReviews - 1);
          this.calculateCounts();
          this.applyFilters();
          this.closeDeleteModal();
          this.showToast('Đã xóa đánh giá thành công khỏi hệ thống', 'success');
        } else {
          this.closeDeleteModal();
          this.showToast('Không thể xóa đánh giá. Vui lòng thử lại!', 'error');
        }
      },
      error: (err) => {
        this.isDeletingReview = false;
        this.closeDeleteModal();
        console.error('Error deleting review:', err);
        this.showToast('Lỗi khi gửi yêu cầu xóa đánh giá', 'error');
      }
    });
  }

  // Visual Helpers
  getInitials(name?: string): string {
    if (!name || !name.trim()) return 'KH';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  getAvatarBg(id: number): string {
    const bgs = [
      'bg-slate-900 text-amber-400 border-amber-400/30',
      'bg-sky-900 text-sky-200 border-sky-400/30',
      'bg-emerald-900 text-emerald-200 border-emerald-400/30',
      'bg-amber-900 text-amber-200 border-amber-400/30',
      'bg-indigo-900 text-indigo-200 border-indigo-400/30',
    ];
    return bgs[id % bgs.length];
  }

  getSentimentBadge(sentiment?: string, rating?: number): { label: string; class: string } {
    if (sentiment === 'POSITIVE' || (!sentiment && (rating || 0) >= 5)) {
      return {
        label: 'Cảm xúc: Tích Cực',
        class: 'bg-emerald-50 text-emerald-700 border-emerald-200'
      };
    }
    if (sentiment === 'NEUTRAL' || (!sentiment && (rating || 0) === 4)) {
      return {
        label: 'Cảm xúc: Trung Lập',
        class: 'bg-slate-100 text-slate-700 border-slate-200'
      };
    }
    return {
      label: 'Cảm xúc: Cần Lưu Ý',
      class: 'bg-rose-50 text-rose-700 border-rose-200'
    };
  }

  getStarRange(rating: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }

  showToast(msg: string, type: 'success' | 'error' | 'info' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }
}
