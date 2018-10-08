# The MIT License
#
# Copyright 2018 yasshy2.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

# RushHour for integration test

FROM nginx

ARG HOSTNAME=localhost
ARG APS_HOST=localhost
ARG APS_PORT=8080

VOLUME /etc/nginx/conf.d
VOLUME /var/log/nginx

ENV CONF /etc/nginx/conf.d/default.conf

ENV HOSTNAME=${HOSTNAME}
ENV APS_HOST=${APS_HOST}
ENV APS_PORT=${APS_PORT}

ENTRYPOINT \
    mkdir -p /etc/nginx/conf.d && \
    echo "server{" > ${CONF} && \
    echo "    listen         80;" >> ${CONF} && \
    echo "    server_name    $HOSTNAME;" >> ${CONF} && \
    echo "    " >> ${CONF} && \
    echo "    proxy_set_header    Host    \$host;" >> ${CONF} && \
    echo "    proxy_set_header    X-Real-IP    \$remote_addr;" >> ${CONF} && \
    echo "    proxy_set_header    X-Forwarded-Host       \$host;" >> ${CONF} && \
    echo "    proxy_set_header    X-Forwarded-Server    \$host;" >> ${CONF} && \
    echo "    proxy_set_header    X-Forwarded-For    \$proxy_add_x_forwarded_for;" >> ${CONF} && \
    echo "    location / {" >> ${CONF} && \
    echo "        proxy_pass    http://$APS_HOST:$APS_PORT;" >> ${CONF} && \
    echo "    }" >> ${CONF} && \
    echo "    " >> ${CONF} && \
    echo "    error_page   500 502 503 504  /50x.html;" >> ${CONF} && \
    echo "    location = /50x.html {" >> ${CONF} && \
    echo "        root   /usr/share/nginx/html;" >> ${CONF} && \
    echo "    }" >> ${CONF} && \
    echo "}" >> ${CONF} && \
    nginx -g 'daemon off;'