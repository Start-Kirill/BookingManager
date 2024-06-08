FROM swaggerapi/swagger-ui:latest
MAINTAINER Kiryl

ADD /booking-manager/spec/booking-manager.yaml /usr/share/nginx/html/doc/booking-manager.yaml

ENV URLS="[{ url: 'doc/booking-manager.yaml', name: 'BookingManager'}]"
ENV URLS_PRIMARY_NAME="BookingManager"