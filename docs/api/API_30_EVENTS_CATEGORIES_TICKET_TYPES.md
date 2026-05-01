# Events, Categories, Ticket Types APIs

## EventController (base: /events, bearerAuth)

- POST /events (multipart/form-data)
    - Request: EventRequest (@ModelAttribute)
    - Response: ApiResponse<EventResponse>

- GET /events?page={page}&size={size}
- GET /events/search?page={page}&size={size}
    - Query filters: search, name, province, provinceCode, minPrice, maxPrice, startDate, endDate
    - Response: ApiResponse<Page<EventResponse>>

- GET /events/{id}
    - Response: ApiResponse<EventResponse>

- PUT /events/{id} (multipart/form-data)
    - Request: EventRequest
    - Response: ApiResponse<EventResponse>

### Admin

- GET /events/admin/all?page={page}&size={size}&search={search}&status={status}
    - Response: ApiResponse<Page<EventResponse>>

- PATCH /events/{id}/status?status={status}
    - Response: ApiResponse<EventResponse>

### Organizer

- GET /events/organizer/my-events?page={page}&size={size}
    - Response: ApiResponse<Page<EventResponse>>

- GET /events/organizer/stats
    - Response: ApiResponse<OrganizerStatsResponse>

### Blog sync

- GET /events/blog-news?page={page}&size={size}
    - Query params: `page` (default 0), `size` (default 10)
    - Response: ApiResponse<Page<BlogEventResponse>>

Note:

- `/events/{id}` expects a numeric `id` (Long). Do not pass a slug or string there (e.g. `blog-news` will fail conversion to Long).
- Use `/events/blog-news` to get the blog/news list and `/blog/posts/{slug}` to retrieve blog post details by slug.

Examples:

- GET list (blog/news): `/events/blog-news?page=0&size=10`
- GET event by id: `/events/123` (where `123` is numeric event id)
- GET blog post by slug: `/blog/posts/my-event-announcement`

## CategoryController (base: /categories)

- POST /categories
    - Request: CategoryRequest
    - Response: ApiResponse<CategoryResponse>

- GET /categories
    - Response: ApiResponse<List<CategoryResponse>>

- GET /categories/{id}
    - Response: ApiResponse<CategoryResponse>

- PUT /categories/{id}
    - Request: CategoryRequest
    - Response: ApiResponse<CategoryResponse>

- DELETE /categories/{id}
    - Response: ApiResponse<Void>

## TicketTypeController (base: /ticket-types)

- POST /ticket-types
    - Request: TicketTypeRequest
    - Response: ApiResponse<TicketTypeResponse>

- GET /ticket-types/event/{eventId}
    - Response: ApiResponse<List<TicketTypeResponse>>
