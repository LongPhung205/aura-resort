import { Injectable } from '@angular/core';
import {
  ResortItem,
  ResortCategory,
  OfferPackage,
  EliteBenefit,
  CoreValueItem,
  TestimonialItem,
  StatItem,
  CustomerCategoryBenefit,
  CustomerCategoryItem,
} from '../models/home.models';

@Injectable({
  providedIn: 'root',
})
export class HomeMockService {
  private readonly resorts: ResortItem[] = [
    {
      id: 'da-nang',
      name: 'Aura Grand Heritage Đà Nẵng',
      category: 'beach',
      location: 'Vịnh Non Nước, Đà Nẵng',
      imageUrl: '/assets/images/rooms/grand-oceanfront.jpg',
      badges: ['VỊNH NON NƯỚC', '5 SAO CHUẨN QUỐC TẾ'],
      features: [
        'Vị trí sát bờ biển Non Nước với bãi cát trắng mịn riêng tư',
        'Bể bơi vô cực 3 tầng phóng tầm nhìn toàn cảnh đại dương',
        'Spa & trung tâm trị liệu chuẩn Aura Wellness',
      ],
      pricePerNight: 3250000,
      priceDisplay: '3.250.000₫',
      rating: 9.8,
      ratingCount: 1420,
    },
    {
      id: 'phu-quoc',
      name: 'Aura Pearl Sanctuary Phú Quốc',
      category: 'beach',
      location: 'Bãi Dài, Phú Quốc',
      imageUrl: '/assets/images/rooms/sunset-lagoon.jpg',
      badges: ['BÃI DÀI, PHÚ QUỐC', 'BIỆT THỰ RIÊNG BIỆT'],
      features: [
        'Toạ lạc tại bãi biển hoàng hôn riêng biệt, bãi cát mịn hoang sơ',
        'Biệt thự hồ bơi riêng view trọn đại dương hoàng hôn',
        'Nhà hàng ẩm thực Michelin chuẩn quốc tế',
      ],
      pricePerNight: 5850000,
      priceDisplay: '5.850.000₫',
      rating: 9.9,
      ratingCount: 980,
    },
    {
      id: 'sa-pa',
      name: 'Aura Mist Retreat Sa Pa',
      category: 'mountain',
      location: 'Mường Hoa, Sa Pa',
      imageUrl: '/assets/images/rooms/cliffside-sunset.jpg',
      badges: ['MƯỜNG HOA, SA PA', 'HÒA CÙNG THIÊN NHIÊN'],
      features: [
        'Ẩn mình giữa thung lũng mây trập trùng và ruộng bậc thang',
        'Hồ nước nóng sưởi ấm 4 mùa giữa ngàn mây',
        'Ẩm thực đặc sản bản địa kết hợp phong cách Fine Dining',
      ],
      pricePerNight: 2850000,
      priceDisplay: '2.850.000₫',
      rating: 9.7,
      ratingCount: 1150,
    },
    {
      id: 'nha-trang',
      name: 'Aura Azure Bay Nha Trang',
      category: 'bay',
      location: 'Vịnh Ninh Vân, Nha Trang',
      imageUrl: '/assets/images/rooms/presidential-suite.jpg',
      badges: ['VỊNH NINH VÂN, NHA TRANG', 'ĐỘC NHẤT VÔ NHỊ'],
      features: [
        'Khu biệt thự ghềnh đá riêng biệt, đón bình minh tuyệt mỹ',
        'Đưa đón riêng bằng du thuyền cao cấp Aura Yacht',
        'Phục vụ ẩm thực bãi biển & hoạt động thể thao nước',
      ],
      pricePerNight: 4150000,
      priceDisplay: '4.150.000₫',
      rating: 9.8,
      ratingCount: 860,
    },
    {
      id: 'ha-long',
      name: 'Aura Emerald Haven Hạ Long',
      category: 'bay',
      location: 'Vịnh Hạ Long, Quảng Ninh',
      imageUrl: '/assets/images/services/maybach-s680.jpg',
      badges: ['VỊNH HẠ LONG', 'DI SẢN THẾ GIỚI'],
      features: [
        'Biệt thự nổi hướng trọn kỳ quan đá vôi huyền thoại',
        'Đặc quyền du thuyền riêng Aura Private Cruise ngắm hoàng hôn',
        'Thưởng thức hải sản vịnh biển tươi sống chuẩn 5 sao',
      ],
      pricePerNight: 4650000,
      priceDisplay: '4.650.000₫',
      rating: 9.9,
      ratingCount: 1280,
    },
    {
      id: 'hoi-an',
      name: 'Aura Silk Oasis Hội An',
      category: 'beach',
      location: 'Bãi biển Cửa Đại, Hội An',
      imageUrl: '/assets/images/services/yacht-aura-pearl.jpg',
      badges: ['CỬA ĐẠI, HỘI AN', 'KIẾN TRÚC DI SẢN'],
      features: [
        'Khu nghỉ dưỡng ven biển giao thoa tinh hoa kiến trúc phố cổ',
        'Bãi biển riêng tư bình yên với bờ cát trắng phau',
        'Trải nghiệm ẩm thực Cung đình & chèo thuyền hoa đăng',
      ],
      pricePerNight: 3450000,
      priceDisplay: '3.450.000₫',
      rating: 9.8,
      ratingCount: 950,
    },
    {
      id: 'da-lat',
      name: 'Aura Pine Valley Đà Lạt',
      category: 'mountain',
      location: 'Hồ Tuyền Lâm, Đà Lạt',
      imageUrl: '/assets/images/services/lotus-spa.jpg',
      badges: ['HỒ TUYỀN LÂM, ĐÀ LẠT', 'DINH THỰ RỪNG THÔNG'],
      features: [
        'Dinh thự phong cách Pháp cổ điển ẩn mình giữa ngàn thông tĩnh lặng',
        'Hồ bơi nước ấm khoáng nóng ngoài trời hướng mặt hồ',
        'Tiệc trà chiều hoàng gia phong cách quý tộc Âu Châu',
      ],
      pricePerNight: 3850000,
      priceDisplay: '3.850.000₫',
      rating: 9.8,
      ratingCount: 1340,
    },
    {
      id: 'con-dao',
      name: 'Aura Hideaway Côn Đảo',
      category: 'beach',
      location: 'Bãi Đất Dốc, Côn Đảo',
      imageUrl: '/assets/images/services/beach-bbq.jpg',
      badges: ['BÃI ĐẤT DỐC, CÔN ĐẢO', 'THIÊN ĐƯỜNG HOANG SƠ'],
      features: [
        '100% Biệt thự mặt biển biệt lập với hồ bơi vô cực chân trời',
        'Trải nghiệm ngắm rùa đẻ trứng & lặn ngắm san hô quý hiếm',
        'Liệu trình Spa thanh lọc năng lượng hòa quyện đại dương',
      ],
      pricePerNight: 6950000,
      priceDisplay: '6.950.000₫',
      rating: 9.9,
      ratingCount: 820,
    },
    {
      id: 'quy-nhon',
      name: 'Aura Cliffside Quy Nhơn',
      category: 'bay',
      location: 'Ghềnh Ráng, Quy Nhơn',
      imageUrl: '/assets/images/promotions/summer-elite.jpg',
      badges: ['GHỀNH RÁNG, QUY NHƠN', 'VỊNH ĐÁ HOÀNG HÔN'],
      features: [
        'Kiến trúc vách đá độc bản vươn mình ra vịnh biển ngọc bích',
        'Bể bơi vô cực nước mặn trên ghềnh đá đón sóng vỗ',
        'Fine dining ngắm hoàng hôn rực rỡ trên vịnh Quy Nhơn',
      ],
      pricePerNight: 4200000,
      priceDisplay: '4.200.000₫',
      rating: 9.7,
      ratingCount: 760,
    },
    {
      id: 'ha-giang',
      name: 'Aura Sky Cloud Retreat Hà Giang',
      category: 'mountain',
      location: 'Đèo Mã Pí Lèng, Hà Giang',
      imageUrl: '/assets/images/promotions/royal-honeymoon.jpg',
      badges: ['MÃ PÍ LÈNG, HÀ GIANG', 'HÙNG VĨ ĐỆ NHẤT ĐÈO'],
      features: [
        'Tọa độ ngắm trọn vẹn hẻm Tu Sản và dòng sông Nho Quế màu ngọc bích',
        'Thiết kế nhà trình tường cao cấp ấm áp quanh năm',
        'Trải nghiệm văn hóa bản địa độc đáo và tour trekking độc quyền',
      ],
      pricePerNight: 3650000,
      priceDisplay: '3.650.000₫',
      rating: 9.8,
      ratingCount: 690,
    },
    {
      id: 'vinh-hy',
      name: 'Aura Ocean Sanctuary Vĩnh Hy',
      category: 'bay',
      location: 'Vườn Quốc Gia Núi Chúa, Vĩnh Hy',
      imageUrl: '/assets/images/promotions/wellness-zen.jpg',
      badges: ['VỊNH VĨNH HY', 'KHU DỰ TRỮ SINH QUYỂN'],
      features: [
        'Tọa lạc trong lòng khu dự trữ sinh quyển thế giới Núi Chúa',
        'Khu bảo tồn rạn san hô tự nhiên hoàn toàn riêng tư',
        'Sân tập yoga và thiền định trên mỏm đá ngắm bình minh biển',
      ],
      pricePerNight: 5600000,
      priceDisplay: '5.600.000₫',
      rating: 9.9,
      ratingCount: 910,
    },
    {
      id: 'mai-chau',
      name: 'Aura Lotus Valley Mai Châu',
      category: 'mountain',
      location: 'Bản Lác, Mai Châu, Hòa Bình',
      imageUrl: '/assets/images/rooms/grand-oceanfront.jpg',
      badges: ['THUNG LŨNG MAI CHÂU', 'BẢN SẮC TÂY BẮC'],
      features: [
        'Biệt thự gỗ teak cao cấp hướng tầm nhìn thung lũng đồng lúa xanh',
        'Hồ bơi vô cực ngắm mây trôi trên các rặng núi đá vôi',
        'Ẩm thực đặc sắc Tây Bắc kết hợp tinh hoa trà đạo',
      ],
      pricePerNight: 2950000,
      priceDisplay: '2.950.000₫',
      rating: 9.7,
      ratingCount: 890,
    },
  ];

  private readonly stats: StatItem[] = [
    {
      value: '9.8 / 10',
      description: 'Đánh giá xuất sắc từ hơn 15.000+ khách hàng năm 2024',
    },
    {
      value: '100%',
      description: 'Biệt thự hướng biển sở hữu hồ bơi vô cực riêng biệt',
    },
    {
      value: '24/7',
      description: 'Dịch vụ quản gia riêng chuẩn Butler Royal Suite & Villa',
    },
    {
      value: '0₫ ★',
      description: 'Phí hủy đặt phòng đối với thành viên Aura Elite 2024',
    },
  ];

  private readonly customerCategories: CustomerCategoryItem[] = [
    {
      id: 'family',
      name: 'Khách Hàng Gia Đình',
      subtitle: 'AURA FAMILY HARMONY',
      badge: 'GIA ĐÌNH & BẠN BÈ',
      imageUrl: '/assets/images/rooms/sunset-lagoon.jpg',
      icon: 'family_restroom',
    },
    {
      id: 'couple',
      name: 'Cặp Đôi & Tuần Trăng Mật',
      subtitle: 'AURA ROYAL ROMANCE',
      badge: 'LÃNG MẠN & RIÊNG TƯ',
      imageUrl: '/assets/images/rooms/cliffside-sunset.jpg',
      icon: 'favorite',
    },
    {
      id: 'wellness',
      name: 'Khách Trị Liệu & Sức Khỏe',
      subtitle: 'AURA WELLNESS SANCTUARY',
      badge: 'TÁI TẠO NĂNG LƯỢNG',
      imageUrl: '/assets/images/rooms/presidential-suite.jpg',
      icon: 'spa',
    },
    {
      id: 'vvip',
      name: 'Doanh Nhân & VVIP Độc Bản',
      subtitle: 'AURA ELITE RESERVE',
      badge: 'ĐẲNG CẤP THƯỢNG LƯU',
      imageUrl: '/assets/images/services/maybach-s680.jpg',
      icon: 'workspace_premium',
    },
  ];

  private readonly customerBenefits: CustomerCategoryBenefit[] = [
    {
      id: 'silver',
      tier: 'Aura Silver Member',
      categoryName: 'Khách Hàng Khởi Đầu & Trải Nghiệm',
      subtitle: 'Hạng Thẻ Khởi Đầu Kỳ Nghỉ Tinh Tế',
      tag: 'SILVER MEMBER',
      tagColor: 'bg-slate-100 text-slate-700 border-slate-300',
      imageUrl: '/assets/images/services/yacht-aura-pearl.jpg',
      icon: 'shield',
      description: 'Lựa chọn hoàn hảo cho du khách lần đầu đặt phòng trực tiếp tại website Aura để hưởng ưu đãi tức thì.',
      privileges: [
        'Ưu đãi giảm ngay 10% giá phòng khi đặt trực tuyến',
        'Tặng đồ uống chào mừng Aura Signature & trái cây tươi',
        'Tích lũy 5% điểm thưởng Aura Rewards trọn đời',
        'Miễn phí Wi-Fi tốc độ cao & sử dụng hồ bơi vô cực',
      ],
      ctaText: 'Đăng Ký Miễn Phí',
    },
    {
      id: 'gold',
      tier: 'Aura Gold Royal',
      categoryName: 'Khách Hàng Thân Thiết & Gia Đình',
      subtitle: 'Hạng Thẻ Hoàng Gia Trọn Vẹn Gắn Kết',
      tag: 'GOLD ROYAL',
      tagColor: 'bg-amber-100 text-amber-900 border-amber-300',
      imageUrl: '/assets/images/services/lotus-spa.jpg',
      icon: 'stars',
      description: 'Tối ưu cho các kỳ nghỉ gia đình và du khách thân thiết với các đặc quyền nâng hạng và thời gian lưu trú linh hoạt.',
      privileges: [
        'Tự động nâng hạng phòng cao cấp khi còn phòng trống',
        'Nhận phòng sớm từ 10:00 & Trả phòng trễ đến 16:00',
        'Miễn phí bữa sáng Buffet quốc tế hàng ngày cho 2 khách',
        'Tặng voucher trị giá 500.000₫ dịch vụ Aura Spa & Ẩm thực',
      ],
      ctaText: 'Xem Chi Tiết Quyền Lợi',
    },
    {
      id: 'platinum',
      tier: 'Aura Platinum Elite',
      categoryName: 'Khách Cặp Đôi & Nghỉ Dưỡng Trăng Mật',
      subtitle: 'Hạng Thẻ Thượng Lưu Riêng Tư Tuyệt Đối',
      tag: 'PLATINUM ELITE',
      tagColor: 'bg-sky-100 text-sky-900 border-sky-300',
      imageUrl: '/assets/images/services/beach-bbq.jpg',
      icon: 'diamond',
      description: 'Không gian riêng tư biệt lập cùng dịch vụ quản gia chuyên nghiệp mang đến kỳ trăng mật và nghỉ dưỡng cặp đôi thăng hoa.',
      privileges: [
        'Quản gia riêng biệt 24/7 (Dedicated Royal Butler)',
        'Đưa đón sân bay 2 chiều bằng xe sang Limousine riêng',
        'Miễn phí 100% phí hủy đặt phòng trước 24 giờ nhận phòng',
        'Set Champagne hoàng gia chào mừng & bữa tối nến lãng mạn',
      ],
      ctaText: 'Xem Chi Tiết Quyền Lợi',
    },
    {
      id: 'diamond',
      tier: 'Aura Black Diamond Reserve',
      categoryName: 'Khách Doanh Nhân & VVIP Độc Bản',
      subtitle: 'Đặc Quyền Tối Thượng Không Giới Hạn',
      tag: 'BLACK DIAMOND',
      tagColor: 'bg-slate-900 text-amber-300 border-slate-700',
      imageUrl: '/assets/images/promotions/summer-elite.jpg',
      icon: 'workspace_premium',
      description: 'Đặc quyền tối thượng dành cho giới tinh hoa doanh nhân và khách hàng thượng lưu cao cấp nhất của Aura Hotels & Resorts.',
      privileges: [
        'Đặc quyền du thuyền riêng Aura Private Yacht ngắm hoàng hôn',
        'Đầu bếp sao Michelin phục vụ ẩm thực riêng biệt tại Villa',
        'Cam kết luôn có phòng ưu tiên ngay cả trong mùa cao điểm',
        'Tặng đêm nghỉ dưỡng sinh nhật miễn phí tại Presidential Villa',
      ],
      ctaText: 'Liên Hệ Chuyên Viên VVIP',
    },
  ];

  private readonly offers: OfferPackage[] = [
    {
      id: 'early-bird',
      badge: 'ƯU ĐÃI ĐẶT SỚM MÙA HÈ',
      title: 'Early Bird Mùa Hè 2025: Đặt Trước 45 Ngày',
      description:
        'Lên kế hoạch kỳ nghỉ sớm tại các vịnh biển đẹp nhất Việt Nam để nhận đặc quyền ẩm thực và chăm sóc sức khỏe thượng lưu.',
      benefits: [
        'Tiết kiệm đến 35% giá phòng khi đặt sớm 45 ngày',
        'Miễn phí bữa sáng buffet chuẩn quốc tế mỗi ngày',
        'Tặng voucher trị giá 500.000₫ dịch vụ spa & ẩm thực',
        'Miễn phí nhận phòng sớm & trả phòng trễ',
      ],
      buttonText: 'Áp Dụng Ưu Đãi Ngay',
      imageUrl: '/assets/images/promotions/royal-honeymoon.jpg',
      featured: true,
    },
    {
      id: 'honeymoon',
      badge: 'GÓI TRẢI NGHIỆM ĐỘC QUYỀN',
      title: 'Trọn Vẹn Tuần Trăng Mật Hoàng Gia',
      description:
        'Bữa tối lãng mạn dưới ánh nến bờ biển, liệu trình spa cặp đôi 90 phút, xe limousine đưa đón sân bay và rượu vang thượng hạng.',
      priceDisplay: 'Chỉ từ: 8.500.000₫ / gói 3N2Đ',
      buttonText: 'Khám phá ngay',
      imageUrl: '/assets/images/promotions/wellness-zen.jpg',
    },
    {
      id: 'family',
      badge: 'KỲ NGHỈ GIA ĐÌNH',
      title: 'Gia Đình Sum Vầy (Aura Family Harmony)',
      description:
        'Biệt thự 2 phòng ngủ sát biển, miễn phí câu lạc bộ trẻ em Aura Kids Club, tiệc BBQ hải sản sân vườn gia đình ấm cúng.',
      priceDisplay: 'Chỉ từ: 6.200.000₫ / gói 3N2Đ',
      buttonText: 'Khám phá ngay',
      imageUrl: '/assets/images/rooms/grand-oceanfront.jpg',
    },
  ];

  private readonly eliteBenefits: EliteBenefit[] = [
    {
      id: 'upgrade',
      title: 'Nâng Hạng Phòng Miễn Phí',
      description:
        'Ưu tiên nhận phòng sớm từ 10:00, trả phòng trễ đến 16:00 & tự động nâng hạng phòng cao cấp khi còn phòng.',
      icon: 'upgrade',
      tag: 'ĐẶC QUYỀN HẠNG SUITE',
      imageUrl: '/assets/images/rooms/sunset-lagoon.jpg',
    },
    {
      id: 'stay-24h',
      title: 'Linh Hoạt 24 Giờ Lưu Trú',
      description:
        'Trọn vẹn 24h nghỉ dưỡng tính từ giờ check-in thực tế mà không giới hạn mốc check-in/out cố định.',
      icon: 'schedule',
      tag: 'TỰ DO LỊCH TRÌNH',
      imageUrl: '/assets/images/rooms/cliffside-sunset.jpg',
    },
    {
      id: 'rewards',
      title: 'Tích Điểm Thưởng Đổi Đêm',
      description:
        'Tích luỹ điểm Aura Rewards sau mỗi đêm nghỉ để quy đổi các đêm nghỉ miễn phí hoặc dịch vụ ẩm thực, spa.',
      icon: 'diamond',
      tag: 'AURA REWARDS',
      imageUrl: '/assets/images/rooms/presidential-suite.jpg',
    },
    {
      id: 'gift',
      title: 'Quà Chào Mừng Thượng Hạng',
      description:
        'Đồ uống chào mừng cao cấp, hoa tươi, trái cây thượng hạng và set quà Aura Signature độc quyền tại phòng.',
      icon: 'redeem',
      tag: 'AURA SIGNATURE',
      imageUrl: '/assets/images/services/maybach-s680.jpg',
    },
  ];

  private readonly coreValues: CoreValueItem[] = [
    {
      id: 'best-price',
      number: '01',
      subtitle: 'GIÁ TRỊ NGUYÊN BẢN',
      title: 'Đảm Bảo Giá Trực Tiếp Tốt Nhất',
      description:
        'Cam kết mức giá tốt nhất khi đặt trực tiếp tại website của chúng tôi. Hoàn lại 100% chênh lệch nếu tìm thấy giá thấp hơn.',
      icon: 'savings',
      imageUrl: '/assets/images/services/yacht-aura-pearl.jpg',
    },
    {
      id: 'butler',
      number: '02',
      subtitle: 'NGHỆ THUẬT PHỤC VỤ',
      title: 'Quản Gia Riêng Biệt 24/7',
      description:
        'Đội ngũ quản gia đạt chứng chỉ quốc tế Magisters sẵn sàng phục vụ từng yêu cầu nhỏ nhất của bạn 24/7 với sự tận tâm và kín đáo.',
      icon: 'room_service',
      imageUrl: '/assets/images/services/lotus-spa.jpg',
    },
    {
      id: 'michelin',
      number: '03',
      subtitle: 'MỸ VỊ ĐỈNH CAO',
      title: 'Đỉnh Cao Ẩm Thực Michelin',
      description:
        'Hệ thống nhà hàng ẩm thực đạt sao Michelin với các siêu đầu bếp quốc tế, tôn vinh ẩm thực địa phương kết hợp kỹ thuật phương Tây.',
      icon: 'restaurant',
      imageUrl: '/assets/images/services/beach-bbq.jpg',
    },
    {
      id: 'sustainable',
      number: '04',
      subtitle: 'HÒA NHỊP THIÊN NHIÊN',
      title: 'Bảo Tồn Sinh Thái Bền Vững',
      description:
        'Tất cả khu resort phát triển theo mô hình sinh thái xanh, bảo tồn tối đa hệ sinh thái tự nhiên và sử dụng 100% năng lượng tái tạo.',
      icon: 'eco',
      imageUrl: '/assets/images/promotions/summer-elite.jpg',
    },
  ];

  private readonly testimonials: TestimonialItem[] = [
    {
      id: 'test-1',
      quote:
        'Kỳ nghỉ tại Aura Pearl Sanctuary Phú Quốc đã định nghĩa lại khái niệm xa hoa đích thực đối với tôi. Mọi chi tiết, từ hương thơm phòng ngủ đến nụ cười của nhân viên đều mang đến sự tinh tế đến không ngờ!',
      authorName: 'Ông Trần Tuấn Vũ',
      authorTitle: 'CEO Tập đoàn Xây dựng An Phát - Hội viên Aura Black Elite',
      stars: 5,
      avatarUrl: '/assets/images/staff/avatar-nam.jpg',
    },
    {
      id: 'test-2',
      quote:
        'Aura Mist Retreat Sa Pa khiến tôi cảm giác như mình đang ở một resort tại Thụy Sĩ. Bể bơi nước ấm nhìn ra thung lũng mây là góc ngắm bình minh đẹp nhất mà tôi từng trải nghiệm ở Châu Á.',
      authorName: 'Bà Lê Mai Phương',
      authorTitle: 'Giám đốc Sáng tạo - Tạp chí Kiến trúc & Nghệ thuật',
      stars: 5,
      avatarUrl: '/assets/images/staff/avatar-hoa.jpg',
    },
    {
      id: 'test-3',
      quote:
        'Từ dịch vụ quản gia cá nhân ở Vịnh Ninh Vân đến Aura Grand Heritage Đà Nẵng, chất lượng luôn nhất quán. Dịch vụ đón tiễn chuẩn limousine và bãi biển riêng tư thật tuyệt vời!',
      authorName: 'GS. TS David Miller & Anna Miller',
      authorTitle: 'Du khách quốc tế từ Singapore',
      stars: 5,
      avatarUrl: '/assets/images/staff/avatar-quang.jpg',
    },
  ];

  getResorts(): ResortItem[] {
    return this.resorts;
  }

  getResortsByCategory(category: ResortCategory): ResortItem[] {
    if (category === 'all') {
      return this.resorts;
    }
    return this.resorts.filter((r) => r.category === category);
  }

  getStats(): StatItem[] {
    return this.stats;
  }

  getCustomerBenefits(): CustomerCategoryBenefit[] {
    return this.customerBenefits;
  }

  getCustomerCategories(): CustomerCategoryItem[] {
    return this.customerCategories;
  }

  getOffers(): OfferPackage[] {
    return this.offers;
  }

  getEliteBenefits(): EliteBenefit[] {
    return this.eliteBenefits;
  }

  getCoreValues(): CoreValueItem[] {
    return this.coreValues;
  }

  getTestimonials(): TestimonialItem[] {
    return this.testimonials;
  }
}
