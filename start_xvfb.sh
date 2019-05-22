#!/usr/bin/env bash

rm -f /tmp/.X1-lock
Xvfb :1 -screen 0 1600x1200x24 &
