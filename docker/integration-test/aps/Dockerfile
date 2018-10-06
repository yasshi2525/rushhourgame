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

FROM openjdk:8-jdk

ENV PAYARA_PATH /opt/payara5
ENV PAYARA_DOMAIN domain1
ENV ADMIN_USER admin
ENV ADMIN_PASSWORD admin

ENV DBHOST localhsot
ENV DBPORT 3306
ENV DBDATABASE rushhourittest
ENV DBUSER rushhourittest
ENV DBPASSWORD rushhourittest

ENV DBDRIVER_URL=https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar
ENV DBDRIVER_JAR=mariadb-java-client.jar
ENV DBDRIVER_CLASS=org.mariadb.jdbc.MariaDbDataSource

ENV RUSHHOUR_CONTEXTROOT /RushHourGame
ENV OAUTHSERVER_CONTEXTROOT /OAuth

RUN \
 apt-get update && \ 
 apt-get install -y unzip 

RUN \ 
 useradd -b /opt -m -s /bin/bash -d ${PAYARA_PATH} payara && echo payara:payara | chpasswd && \
 mkdir ${PAYARA_PATH}/conf

# specify Payara version to download
ENV PAYARA_PKG https://s3-eu-west-1.amazonaws.com/payara.fish/payara-5-prerelease.zip
ENV PAYARA_VERSION 5-SNAPSHOT

ENV PKG_FILE_NAME payara-full-${PAYARA_VERSION}.zip

# Download Payara Server and install
RUN \
 wget -q -O /opt/${PKG_FILE_NAME} ${PAYARA_PKG} && \
 unzip -qq /opt/${PKG_FILE_NAME} -d /opt && \
 wget -q -O ${PAYARA_PATH}/glassfish/lib/${DBDRIVER_JAR} $DBDRIVER_URL && \ 
 chown -R payara:payara /opt && \
 rm /opt/${PKG_FILE_NAME}

USER payara
WORKDIR ${PAYARA_PATH}

# set credentials to admin/admin 

RUN echo 'AS_ADMIN_PASSWORD=\n\
AS_ADMIN_NEWPASSWORD='${ADMIN_PASSWORD}'\n\
EOF\n'\
>> /opt/tmpfile

RUN echo 'AS_ADMIN_PASSWORD='${ADMIN_PASSWORD}'\n\
EOF\n'\
>> /opt/pwdfile

ENV ASOPTS "--user ${ADMIN_USER} --passwordfile=/opt/pwdfile"

 # domain1
RUN ${PAYARA_PATH}/bin/asadmin --user ${ADMIN_USER} --passwordfile=/opt/tmpfile change-admin-password && \
 ${PAYARA_PATH}/bin/asadmin start-domain ${PAYARA_DOMAIN} && \
 ${PAYARA_PATH}/bin/asadmin ${ASOPTS} enable-secure-admin && \
 ${PAYARA_PATH}/bin/asadmin stop-domain ${PAYARA_DOMAIN} && \
 rm -rf ${PAYARA_PATH}/glassfish/domains/${PAYARA_DOMAIN}/osgi-cache

# cleanup
RUN rm /opt/tmpfile

# Default payara ports to expose
EXPOSE 4848 8009 8080 8181

VOLUME /opt/payara5/conf
VOLUME /opt/payara5/glassfish/domains/domain1/autodeploy
VOLUME /opt/payara5/glassfish/domains/domain1/logs
    
ENTRYPOINT \
    ${PAYARA_PATH}/bin/asadmin start-domain ${PAYARA_DOMAIN} && \
    ${PAYARA_PATH}/bin/asadmin ${ASOPTS} create-jdbc-connection-pool \
        --datasourceclassname ${DBDRIVER_CLASS} \
        --restype javax.sql.XADataSource \
        --property user=${DBUSER}:password=${DBPASSWORD}:databaseName=${DBDATABASE}:portNumber=${DBPORT}:serverName=${DBHOST} \
        RushHourGamePool && \
    ${PAYARA_PATH}/bin/asadmin ${ASOPTS} ping-connection-pool RushHourGamePool && \
    ${PAYARA_PATH}/bin/asadmin ${ASOPTS} create-jdbc-resource \ 
        --connectionpoolid RushHourGamePool jdbc/RushHourGame && \
    ${PAYARA_PATH}/bin/asadmin ${ASOPTS} create-managed-executor-service concurrent/RushHourGame && \
    ${PAYARA_PATH}/bin/asadmin ${ASOPTS} create-managed-executor-service --maximumpoolsize 1 concurrent/RushHourGameRoute && \
    ${PAYARA_PATH}/bin/asadmin stop-domain ${PAYARA_DOMAIN} && \
    ${PAYARA_PATH}/bin/asadmin start-domain -v ${PAYARA_DOMAIN}
