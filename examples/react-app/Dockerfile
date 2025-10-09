FROM node:20
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
# build the app for production
RUN npm run build 
FROM nginx:stable-alpine
COPY --from=0 /usr/src/app/build /usr/share/nginx/html

# Expose the port React will run on
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]