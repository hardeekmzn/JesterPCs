package io.virinchi.jesterpcs.controller;

import io.virinchi.jesterpcs.dao.ReviewDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {

    private final ReviewDAO reviewDAO;

    public ReviewController(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    // Add product review
    @PostMapping("/product/{productId}/review")
    public String addReview(
            @PathVariable int productId,
            @RequestParam int rating,
            @RequestParam String reviewText,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object userIdObject =
                session.getAttribute("loggedInUserId");

        if (userIdObject == null) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please log in to write a review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        int userId =
                (Integer) userIdObject;

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please select a rating between 1 and 5."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (reviewText == null ||
                reviewText.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please write something in your review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (reviewText.trim().length() > 2000) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Review cannot be longer than 2000 characters."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        // User must have purchased and received the product
        if (!reviewDAO.hasPurchasedProduct(
                userId,
                productId)) {

            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "You can only review products you have purchased."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        // User can only review the same product once
        if (reviewDAO.hasReviewedProduct(
                userId,
                productId)) {

            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "You have already reviewed this product."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        reviewDAO.addReview(
                productId,
                userId,
                rating,
                reviewText.trim()
        );

        redirectAttributes.addFlashAttribute(
                "reviewSuccess",
                "Your review has been posted."
        );

        return "redirect:/product/" + productId + "#reviews";
    }

    // Edit user's own review
    @PostMapping("/review/{reviewId}/edit")
    public String editReview(
            @PathVariable int reviewId,
            @RequestParam int productId,
            @RequestParam int rating,
            @RequestParam String reviewText,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object userIdObject =
                session.getAttribute("loggedInUserId");

        if (userIdObject == null) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please log in to edit your review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        int userId =
                (Integer) userIdObject;

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please select a rating between 1 and 5."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (reviewText == null ||
                reviewText.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please write something in your review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (reviewText.trim().length() > 2000) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Review cannot be longer than 2000 characters."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        int updatedRows =
                reviewDAO.updateReview(
                        reviewId,
                        userId,
                        rating,
                        reviewText.trim()
                );

        if (updatedRows == 0) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "You cannot edit this review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        redirectAttributes.addFlashAttribute(
                "reviewSuccess",
                "Your review has been updated."
        );

        return "redirect:/product/" + productId + "#reviews";
    }

    // Delete user's own review
    @PostMapping("/review/{reviewId}/delete")
    public String deleteReview(
            @PathVariable int reviewId,
            @RequestParam int productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object userIdObject =
                session.getAttribute("loggedInUserId");

        if (userIdObject == null) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please log in to delete your review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        int userId =
                (Integer) userIdObject;

        int deletedRows =
                reviewDAO.deleteReview(
                        reviewId,
                        userId
                );

        if (deletedRows == 0) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "You cannot delete this review."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        redirectAttributes.addFlashAttribute(
                "reviewSuccess",
                "Your review has been deleted."
        );

        return "redirect:/product/" + productId + "#reviews";
    }

    // Add comment to review
    @PostMapping("/review/{reviewId}/comment")
    public String addComment(
            @PathVariable int reviewId,
            @RequestParam int productId,
            @RequestParam String commentText,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object userIdObject =
                session.getAttribute("loggedInUserId");

        if (userIdObject == null) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please log in to comment on reviews."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        int userId =
                (Integer) userIdObject;

        if (!reviewDAO.reviewExists(reviewId)) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "The review could not be found."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (commentText == null ||
                commentText.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please write something in your comment."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (commentText.trim().length() > 1000) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Comment cannot be longer than 1000 characters."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        reviewDAO.addComment(
                reviewId,
                userId,
                commentText.trim()
        );

        redirectAttributes.addFlashAttribute(
                "commentSuccess",
                "Your comment has been posted."
        );

        return "redirect:/product/" + productId + "#reviews";
    }

    // Upvote review
    @PostMapping("/review/{reviewId}/upvote")
    public String upvoteReview(
            @PathVariable int reviewId,
            @RequestParam int productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object userIdObject =
                session.getAttribute("loggedInUserId");

        if (userIdObject == null) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "Please log in to upvote reviews."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        int userId =
                (Integer) userIdObject;

        if (!reviewDAO.reviewExists(reviewId)) {
            redirectAttributes.addFlashAttribute(
                    "reviewError",
                    "The review could not be found."
            );

            return "redirect:/product/" + productId + "#reviews";
        }

        if (reviewDAO.hasUpvoted(
                reviewId,
                userId)) {

            reviewDAO.removeUpvote(
                    reviewId,
                    userId
            );

        } else {

            reviewDAO.addUpvote(
                    reviewId,
                    userId
            );
        }

        return "redirect:/product/" + productId + "#reviews";
    }
}