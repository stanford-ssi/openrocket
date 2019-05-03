FROM openjdk

RUN yum install -y \
	libXext.x86_64 \
	libXrender.x86_64 \
	libXtst.x86_64 \
	java-1.8.0-openjdk-headless.x86_64 \
	mesa-dri-drivers \
	ant

RUN curl http://vault.centos.org/6.2/os/x86_64/Packages/xorg-x11-server-Xvfb-1.10.4-6.el6.x86_64.rpm -o xvfb.rpm && \
     yum localinstall -y xvfb.rpm

EXPOSE 8000
ENV DISPLAY=:1

COPY . /usr/src/app
WORKDIR /usr/src/app

ENV CLASSPATH=swing/bin:swing/lib/jogl/jogl-all.jar:swing/lib/iText-5.0.2.jar:swing/lib/jcommon-1.0.18.jar:swing/lib/jfreechart-1.0.15.jar:swing/lib/OrangeExtensions-1.2.jar:swing/lib/jogl/gluegen-rt.jar:core/bin:core/lib-extra/RXTXcomm.jar:core/resources:core/lib/opencsv-2.3.jar:core/lib/guice-3.0.jar:core/lib/guice-multibindings-3.0.jar:core/lib/javax.inject.jar:core/lib/aopalliance.jar:core/lib/slf4j-api-1.7.5.jar:core/lib/annotation-detector-3.0.2.jar:lib-test/hamcrest-core-1.3.0RC1.jar:lib-test/hamcrest-library-1.3.0RC1.jar:lib-test/jmock-2.6.0-RC2.jar:lib-test/jmock-junit4-2.6.0-RC2.jar:lib-test/junit-dep-4.8.2.jar:lib-test/test-plugin.jar:lib-test/uispec4j-2.3-jdk16.jar:swing/lib/logback-classic-1.0.12.jar:swing/lib/logback-core-1.0.12.jar:swing/resources:swing/lib/miglayout-4.0-swing.jar:swing/lib/rsyntaxtextarea-2.5.6.jar:swing/build/classes
RUN ant build
CMD ["sh", "run_server.sh"]
