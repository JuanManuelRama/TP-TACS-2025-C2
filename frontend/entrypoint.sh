#!/bin/sh
# Reemplaza la variable NGINX $BACKEND_URL con el valor de la variable de entorno de Docker/ECS
# (La variable de entorno se llamará BACKEND_URL)

envsubst '${BACKEND_URL}' < /etc/nginx/conf.d/default.conf > /etc/nginx/conf.d/default.conf.temp
mv /etc/nginx/conf.d/default.conf.temp /etc/nginx/conf.d/default.conf

# Ejecuta Nginx
exec nginx -g "daemon off;"