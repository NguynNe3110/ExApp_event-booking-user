# Statistics & Blog APIs

## StatisticsController

- GET /statistics-event/by-status/{quarter}/{year}
    - Response: ApiResponse<EventStatusStatsResponse>

- GET /statistics-event/by-temporal/{dayOfWeek}
    - Response: ApiResponse<EventTemporalStatsResponse>

- GET /statistics-revenue/{id_organizer}
    - Response: ApiResponse<List<EventRevenueStatsOrganizerResponse>>

- GET /statistics-revenue/admin
    - Response: ApiResponse<EventRevenueStatsAdminResponse>

## Blog / Events (blog endpoints)

- GET /events/blog-news
    - Query params: `page` (default 0), `size` (default 10)
    - Response: ApiResponse<Page<BlogEventResponse>>

## BlogController (base: /blog)

- GET /blog/posts?page={page}&size={size}
    - Query params: `page` (default 0), `size` (default 10)
    - Response: ApiResponse<Page<BlogPostResponse>>

- GET /blog/posts/{slug}
    - Response: ApiResponse<BlogPostResponse>

- GET /blog/tags
    - Response: ApiResponse<List<BlogTagResponse>>

- GET /blog/organizer/posts?page={page}&size={size}
    - Response: ApiResponse<Page<BlogPostResponse>>

- POST /blog/organizer/posts
    - Request: BlogPostRequest
    - Response: ApiResponse<BlogPostResponse>

- GET /blog/admin/posts?page={page}&size={size}
    - Response: ApiResponse<Page<BlogPostResponse>>

- POST /blog/admin/posts
    - Request: BlogPostRequest
    - Response: ApiResponse<BlogPostResponse>

- PUT /blog/admin/posts/{id}
    - Request: BlogPostRequest
    - Response: ApiResponse<BlogPostResponse>

- PATCH /blog/admin/posts/{id}/publish
    - Response: ApiResponse<BlogPostResponse>

- PATCH /blog/admin/posts/{id}/reject
    - Response: ApiResponse<BlogPostResponse>

- PATCH /blog/admin/posts/{id}/archive
    - Response: ApiResponse<BlogPostResponse>

- DELETE /blog/admin/posts/{id}
    - Response: ApiResponse<Void>

- POST /blog/admin/tags
    - Request: BlogTagRequest
    - Response: ApiResponse<BlogTagResponse>
