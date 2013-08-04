package com.example.androidexamples;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.util.Log;


public class DataExchangeSendHTTPRequests {

	private String _filePath, _url, _method;
	private URL _url_obj;
	HttpURLConnection _con;
	BufferedInputStream _bufferedInputStream;
	FileInputStream _fileInputStream;
	File _localFile;
	byte[] _bytes;
	long _size;
	boolean _sizeSet;

	public DataExchangeSendHTTPRequests(String url) {
		// TODO Auto-generated constructor stub
		
		_url = url;
		
		try {
			_url_obj = new URL(url);
			_con = (HttpURLConnection) _url_obj.openConnection();
			_con.setDoOutput(true);
			
		} catch (MalformedURLException e) {
			Log.wtf("URL object", "The URL has the wrong format. URL="+_url);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Log.wtf("_con", "Initialization failed. URL="+_url);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public DataExchangeSendHTTPRequests(String method, String sourceFilePath, String url) {
		// TODO Auto-generated constructor stub
		this(url);
		
		setMethod(method);
		setSourceFile(sourceFilePath);
		
	}
	
	public void setMethod(String method) {
		_method = method;
		try {
			_con.setRequestMethod(_method);
		} catch (ProtocolException e) {
			Log.w("_con", "Protocol error while setting method="+_method);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setURL(String url) {
		_url = url;
	}
	
	public void setSourceFile(String absolutePath) {
		_filePath = absolutePath;
		_localFile = new File (_filePath);
		
		try {
			_fileInputStream = new FileInputStream(absolutePath);
			_sizeSet = false;
		} catch (FileNotFoundException e) {
			Log.w("setSourceFile", "File not found: "+absolutePath);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_bufferedInputStream = new BufferedInputStream(_fileInputStream);
	}

	public void setFileInputStream(FileInputStream stream, long size) {
		_fileInputStream = stream;
		_bufferedInputStream = new BufferedInputStream(_fileInputStream);
		_size = size;
		_sizeSet = true;
	}
	
	public void setBufferedInputStream(BufferedInputStream stream, long size) {
		_bufferedInputStream = stream;
		_size = size;
		_sizeSet = true;
	}
	public void setData (String data) {
		_bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(data.getBytes()));
		_size = data.length();
		_sizeSet = true;
	}
	
	public void reset() {
		_method = _url = _filePath = "";
	}
	
	
	
	public void send() {
		long i;
		_bytes = new byte[10240];
		_con.setDoOutput(true);

		if (!_sizeSet)
			_size = _localFile.length();
		
		for (i = 0; i<_size; i+=_bytes.length) {
			if (_size - i < _bytes.length) 
				_bytes = new byte[(int) (_size - i)];
			
			
			// read from the input local file
			try {
				_bufferedInputStream.read(_bytes);
			} catch (IOException e) {
				Log.w("send (BufferedInputStream)", "Error at reading from the stream");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// write to the HTTP request
			try {
				_con.getOutputStream().write(_bytes);
			} catch (IOException e) {
				Log.w("send (HttpURLConnection)", "Error at writing to the stream");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			_bufferedInputStream.close();
		} catch (IOException e) {
			Log.w("send (HttpURLConnection)", "Error at closing to the stream");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public InputStream getInputStreamResponse () {
		try {
			return _con.getInputStream();
		} catch (IOException e) {
			Log.w("HttpURLConnection", "Error while reading the response.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
