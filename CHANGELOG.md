# Changelog

## 2026-04-12

### Initial Project Snapshot

- Initialized the repository and core project structure.
- Added product, architecture, development, and deployment documents.
- Built the Spring Boot backend foundation with Docker deployment support.
- Added admin authentication, order management, reservation management, dish management, and category management APIs.
- Scaffolded the Vue 3 admin console with login, dashboards, management tabs, and detail dialogs.
- Scaffolded the WeChat mini-program with ordering, room delivery, reservations, and history pages.
- Fixed seed-data encoding issues for Chinese text and added a local MySQL admin account for development access.

### Commit Policy

- Going forward, every meaningful change will include:
- a git commit
- an updated changelog entry in this file

### Banquet Follow-up Support

- Added backend banquet follow-record entity, mapper, service, and admin APIs.
- Added admin UI support for viewing banquet follow-up records inside the banquet detail dialog.
- Added an admin dialog for creating banquet follow-up notes and optional next-follow timestamps.
- Extended development documentation to reflect banquet follow-up support.

### Order Confirmation Flow

- Added a mini-program order confirmation page before order submission.
- Moved order submission logic out of the cart page into the new confirmation page.
- Added editable contact, phone, and remark fields for order confirmation.
- Updated project documentation to reflect the new confirmation step.

### Checkout UX Improvements

- Improved the order confirmation page with item amount, delivery fee, and payable total breakdown.
- Added a direct "view my orders" action on the order success page.
- Updated development notes to reflect the improved checkout flow.

### Reservation Detail Support

- Added user-facing reservation detail APIs for private-room and banquet records.
- Added a mini-program reservation detail page reachable from the reservation history list.
- Improved the reservation history page with direct detail navigation links.

### Admin Order Filtering

- Added backend admin order filtering by order status and order scene.
- Added admin UI filter controls for order status and ordering scene.
- Improved admin order list usability for day-to-day operations.

### Admin Status and Follow-up Display

- Added banquet follow-record creation timestamps to backend responses.
- Improved admin tables with readable status labels and color tags.
- Extended banquet detail view with follow-up record timestamps for easier tracking.

### Admin Reservation Filtering

- Added backend filtering for private-room reservations by reservation status.
- Added backend filtering for banquet reservations by follow-up status.
- Added admin UI filter controls and reset actions for both reservation tabs.

### Admin Reservation Readability

- Added private-room names and localized timeslot labels to reservation responses.
- Updated admin reservation list and detail dialogs to show readable room and timeslot text.

### Admin Order Readability

- Updated admin order tables and detail dialogs to show localized order-scene labels.
- Added a clearer empty-state fallback for private-room preorder dishes in the detail dialog.

### Admin Reservation Overview

- Expanded banquet reservation tables with direct phone and budget visibility.
- Highlighted private-room deposit amounts in reservation detail dialogs.

### Admin Detail Actions

- Formatted admin dates and amounts consistently across order and reservation views.
- Added direct status action buttons inside order, private-room, and banquet detail dialogs.

### Admin Quick Search

- Added local keyword search for orders, private-room reservations, and banquet reservations.
- Search supports matching business numbers, contact names, and phone numbers directly in the admin console.

### Admin Dish Filtering

- Added local quick search for dish names and subtitles in the admin console.
- Added category-based filtering for the dish management table.

### Admin Reservation Results Feedback

- Added result counts for private-room and banquet reservation filters.
- Added clearer empty-state messages when no reservation records match the current filters.

### Admin List Results Feedback

- Added result counts for order and dish management filters.
- Added clearer empty-state messages for empty order and dish filter results.

### Admin Stats Consistency

- Updated dashboard stat cards to reflect active order and dish filters.
- Added category list result feedback and empty-state messaging for consistency.

### Admin Category Search And Copy

- Added keyword search for category management in the admin console.
- Added one-click phone copy actions in the order management list.

### Admin Reservation Contact Actions

- Added one-click phone copy actions in private-room and banquet reservation lists.
- Aligned contact handling workflows across order and reservation management screens.

### Admin Detail Contact Actions

- Added one-click phone copy actions inside order, private-room, and banquet detail dialogs.
- Made contact handling consistent between list views and detail views.

### Admin Business Number Actions

- Added one-click copy actions for order numbers and reservation numbers in both lists and detail dialogs.
- Added an explicit empty-state message for banquet follow-up records in the detail dialog.

### Admin Status Summary Filters

- Added clickable status summary pills for orders, private-room reservations, and banquet reservations.
- Status summaries now show counts and can toggle the matching filter directly from the list page.

### Extended Admin Status Summaries

- Added clickable status summary pills for category enablement and dish shelf status.
- Updated dashboard stat cards to follow the active management tab and current filtered results more closely.

### Admin Menu Management Enhancements

- Added quick-copy actions for category names and dish names in menu management tables.
- Expanded dish management with recommendation and room-delivery visibility in the list view.
- Added direct recommendation and room-delivery switches to the dish editing dialog.
- Improved category and dish result summaries with status breakdown text.

### Mini Program Menu Experience Enhancements

- Added featured dishes to the home page with quick add-to-cart actions.
- Added clearer dining entry buttons for standard ordering and room-delivery ordering.
- Expanded the menu page with recommendation and room-delivery badges for each dish.

### Mini Program Personal Center Enhancements

- Redesigned the mini-program personal center with grouped dining, room-service, and reservation shortcuts.
- Localized order and reservation cards with readable scene, status, and timeslot labels.
- Added stronger empty-state guidance and quick actions for order and reservation entry points.

### Mini Program Detail Experience Enhancements

- Redesigned order detail pages with localized scene/status labels and grouped amount summaries.
- Redesigned reservation detail pages for both private-room and banquet bookings with clearer grouped information.
- Added better preorder-dish display and empty-state guidance for reservation details.

### Mini Program Reservation Flow Enhancements

- Added clearer summaries, reminders, and sticky submit actions to private-room and banquet booking forms.
- Added client-side validation for key booking fields such as contact name, phone, and guest count.
- Improved booking success feedback with direct navigation to reservation history.

### Mini Program Ordering Flow Enhancements

- Improved cart pages with delivery-scene awareness, item summaries, and empty-state guidance.
- Improved order confirmation with localized scene labels, form validation, and clearer item/amount breakdowns.
- Improved room-delivery recognition with clearer current-room guidance and reset actions.
