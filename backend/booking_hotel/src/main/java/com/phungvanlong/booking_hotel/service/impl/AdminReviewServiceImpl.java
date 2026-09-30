package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.dto.request.ServiceRecoveryRequest;
import com.phungvanlong.booking_hotel.dto.response.ReviewAnalyticsResponse;
import com.phungvanlong.booking_hotel.dto.response.ReviewResponse;
import com.phungvanlong.booking_hotel.dto.response.ServiceRecoveryTicketResponse;
import com.phungvanlong.booking_hotel.entity.Review;
import com.phungvanlong.booking_hotel.entity.ServiceRecoveryTicket;
import com.phungvanlong.booking_hotel.entity.User;
import com.phungvanlong.booking_hotel.exception.ResourceNotFoundException;
import com.phungvanlong.booking_hotel.repository.ReviewRepository;
import com.phungvanlong.booking_hotel.repository.ServiceRecoveryTicketRepository;
import com.phungvanlong.booking_hotel.repository.UserRepository;
import com.phungvanlong.booking_hotel.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final ServiceRecoveryTicketRepository serviceRecoveryTicketRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewAnalyticsResponse getAnalytics() {
        List<Review> reviews = reviewRepository.findAll();
        int total = reviews.size();
        long positive = reviews.stream().filter(r -> "POSITIVE".equalsIgnoreCase(r.getSentiment()) || (r.getRating() != null && r.getRating() >= 4)).count();
        long neutral = reviews.stream().filter(r -> "NEUTRAL".equalsIgnoreCase(r.getSentiment()) || (r.getRating() != null && r.getRating() == 3)).count();
        long negative = reviews.stream().filter(r -> "NEGATIVE".equalsIgnoreCase(r.getSentiment()) || (r.getRating() != null && r.getRating() <= 2)).count();

        double avgRating = total > 0 ? reviews.stream().filter(r -> r.getRating() != null).mapToInt(Review::getRating).average().orElse(0.0) : 0.0;
        double csat = Math.round(avgRating * 2.0 * 10.0) / 10.0; // Thang 10

        int nps = 0;
        if (total > 0) {
            double promoterPct = ((double) positive / total) * 100.0;
            double detractorPct = ((double) negative / total) * 100.0;
            nps = (int) Math.round(promoterPct - detractorPct);
        }

        long repliedCount = reviews.stream().filter(r -> r.getManagementReply() != null && !r.getManagementReply().isBlank()).count();
        double responseRate = total > 0 ? Math.round(((double) repliedCount / total) * 1000.0) / 10.0 : 0.0;

        double avgResponseMinutes = reviews.stream()
                .filter(r -> r.getCreatedAt() != null && r.getRepliedAt() != null)
                .mapToLong(r -> java.time.Duration.between(r.getCreatedAt(), r.getRepliedAt()).toMinutes())
                .average().orElse(0.0);
        avgResponseMinutes = Math.round(avgResponseMinutes * 10.0) / 10.0;

        List<ServiceRecoveryTicketResponse> openTickets = serviceRecoveryTicketRepository.findByStatusOrderByCreatedAtDesc("OPEN")
                .stream()
                .map(ServiceRecoveryTicketResponse::fromEntity)
                .collect(Collectors.toList());

        List<ReviewResponse> latest = reviews.stream()
                .filter(r -> r.getCreatedAt() != null)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());

        return ReviewAnalyticsResponse.builder()
                .csatScore(csat)
                .npsScore(nps)
                .totalReviews(total)
                .positiveCount(positive)
                .neutralCount(neutral)
                .negativeCount(negative)
                .responseRate(responseRate)
                .averageResponseMinutes(avgResponseMinutes)
                .latestReviews(latest)
                .openRecoveryTickets(openTickets)
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse replyReview(Long reviewId, String reply, String managerEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá ID: " + reviewId));

        review.setManagementReply(reply);
        review.setRepliedAt(LocalDateTime.now());

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ServiceRecoveryTicketResponse createRecoveryTicket(ServiceRecoveryRequest request, String managerEmail) {
        Review review = null;
        if (request.getReviewId() != null) {
            review = reviewRepository.findById(request.getReviewId()).orElse(null);
        }

        User manager = null;
        if (request.getAssignedManagerId() != null) {
            manager = userRepository.findById(request.getAssignedManagerId()).orElse(null);
        } else if (managerEmail != null) {
            manager = userRepository.findByEmail(managerEmail).orElse(null);
        }

        ServiceRecoveryTicket ticket = ServiceRecoveryTicket.builder()
                .review(review)
                .guestName(request.getGuestName())
                .roomNumber(request.getRoomNumber())
                .incidentCategory(request.getIncidentCategory() != null ? request.getIncidentCategory() : "SERVICE")
                .issueSummary(request.getIssueSummary())
                .resolutionAction(request.getResolutionAction())
                .assignedManager(manager)
                .slaMinutes(3)
                .status("OPEN")
                .build();

        return ServiceRecoveryTicketResponse.fromEntity(serviceRecoveryTicketRepository.save(ticket));
    }

    @Override
    @Transactional
    public ServiceRecoveryTicketResponse resolveRecoveryTicket(Long ticketId, Integer resolutionMinutes) {
        ServiceRecoveryTicket ticket = serviceRecoveryTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket sự cố ID: " + ticketId));

        ticket.setStatus("RESOLVED");
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setActualResolutionMinutes(resolutionMinutes != null ? resolutionMinutes : 2);

        return ServiceRecoveryTicketResponse.fromEntity(serviceRecoveryTicketRepository.save(ticket));
    }
}
