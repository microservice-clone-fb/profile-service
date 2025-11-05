# Docker Deployment Guide - Profile Service

## Cấu hình Environment Variables

File `.env` đã được tạo với các biến môi trường cần thiết. **Chỉnh sửa file `.env`** với các giá trị thực tế của bạn:

```env
SERVER_PORT=8080
MONGODB_URI=mongodb://your-mongo-host:27017/clone-fb-profile-service
FILE_SERVICE_URL=http://file-service-url:5004
JWT_SECRET_KEY=your-actual-secret-key
```

> **Lưu ý**: File `.env` đã được thêm vào `.gitignore` để bảo mật. Không commit file này lên Git!

## Build và Run Local

```powershell
# Build Docker image
docker build -t profile-service:local .

# Run container với file .env
docker run -p 8080:8080 --env-file .env profile-service:local
```

## Deploy lên Render

### Bước 1: Tạo Web Service
1. Đăng nhập vào [Render](https://render.com)
2. Chọn **New** → **Web Service**
3. Connect với GitHub repository của bạn
4. Chọn branch `production` (hoặc branch bạn muốn deploy)

### Bước 2: Cấu hình Service
- **Name**: `profile-service` (hoặc tên bạn muốn)
- **Environment**: `Docker`
- **Region**: Chọn region gần nhất
- **Branch**: `production`
- **Dockerfile Path**: `Dockerfile` (Render tự detect)

### Bước 3: Environment Variables
Thêm các biến môi trường từ file `.env` của bạn:

**Required:**
- `MONGODB_URI`: Connection string của MongoDB (VD: `mongodb+srv://user:pass@cluster.mongodb.net/dbname`)
- `FILE_SERVICE_URL`: URL của file service
- `JWT_SECRET_KEY`: Secret key cho JWT authentication

**Optional:**
- `SERVER_PORT`: Port của service (Render tự set, để mặc định)
- `CONTEXT_PATH`: Context path (default: `/profile`)
- `LOGGING_LEVEL`: Log level (default: `INFO`)
- `SPRING_PROFILES_ACTIVE`: Spring profile (default: `prod`)

### Bước 4: Deploy
- Render sẽ tự động build và deploy
- Render sẽ set biến `PORT` tự động (thường là 10000)
- Service sẽ available tại: `https://your-service-name.onrender.com`

## Notes
- Alpine image được sử dụng để giảm kích thước image
- Non-root user được tạo để tăng bảo mật
- Dependencies được cache riêng để build nhanh hơn
- Port được set động từ Render's `PORT` environment variable
