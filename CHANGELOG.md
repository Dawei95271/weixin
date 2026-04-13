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
