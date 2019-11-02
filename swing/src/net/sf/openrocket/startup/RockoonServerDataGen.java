package net.sf.openrocket.startup;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RockoonServerDataGen {

    public static void main(String[] args) throws Exception {

    	int sampleEvery = 5; //ms
    	double launchAlt = 60.0;//m
    	double lat = 36.961944; //deg
    	double lng = -119.776730; //deg
    	double rodAng = 0.0523599; //deg
    	double rodLen = 10;//cm
    	double windSpd = 2;//m/s

    	double spinRate = 1500.0;//rpm

    	for(int j=100; j<5000; j+=10)
    	{
    	BufferedWriter writer = new BufferedWriter(new FileWriter("DataOut/Spin"+j+".txt"));
        writer.append(HeadlessRockoonDataGen.generateServerResponse(sampleEvery,j,launchAlt,lat,lng,rodAng,rodLen,windSpd));
        writer.close();
    	}
      }
}
