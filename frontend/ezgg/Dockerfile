FROM node:20-alpine

WORKDIR /app

COPY . .

RUN npm i -g npm
RUN npm install && npm run build
RUN npm install -g serve

EXPOSE 3030

CMD ["serve", "-s", "dist", "-l", "3000"]
