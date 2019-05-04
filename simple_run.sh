#!/usr/bin/env sh

Xvfb :1 -screen 0 1600x1200x24 &
java net.sf.openrocket.startup.HeadlessRockoon
