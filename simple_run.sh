#!/usr/bin/env sh

Xvfb :1 -screen 0 1600x1200x24 &
javac swing/src/net/sf/openrocket/startup/HeadlessRockoon.java
java net.sf.openrocket.startup.HeadlessRockoon
