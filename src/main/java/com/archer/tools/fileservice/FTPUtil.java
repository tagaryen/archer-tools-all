package com.archer.tools.fileservice;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.UUID;

public class FTPUtil {

	/**
	 * @param host ftp ip address
	 * @param port ftp server port
	 * @param user ftp server user
	 * @param password ftp server password
	 * @param path file path
	 * @param filename file name
	 * @return stream
	 * */
    public static InputStream downloadFTP(String host, int port, String user, String password, String path, String filename)
    		throws SftpException, JSchException{
        Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            sftpChannel.cd(path);
            InputStream fileStream = sftpChannel.get(filename);

            sftpChannel.exit();
            
            return fileStream;
        } catch (SftpException | JSchException e) {
        	throw e;
        } finally {
        	if (session != null) {
                session.disconnect();
        	}
        }
    }
    
	/**
	 * @param host ftp ip address
	 * @param port ftp server port
	 * @param user ftp server user
	 * @param password ftp server password
	 * @param path file path
	 * @param filename file name
	 * @param fileStream file content
	 * */
    public static void uploadFTP(String host, int port, String user, String password, String path, String filename, InputStream fileStream) 
    		throws SftpException, JSchException {
    	Session session = null;
    	try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            if(path != null) {
                sftpChannel.cd(path);
            }
            String newFile = toNewFileName(filename);
            sftpChannel.put(fileStream, newFile);
            sftpChannel.exit();
        } catch (SftpException | JSchException e) {
        	throw e;
        } finally {
        	if (session != null) {
                session.disconnect();
        	}
        }
    }

    private static String toNewFileName(String file) {
        String[] splits = file.split("/");
        String finalFile = splits[splits.length-1];
        int idx = finalFile.lastIndexOf('.');
        if(idx < 0 || idx >= finalFile.length() - 1) {
            return finalFile +"-"+ UUID.randomUUID().toString();
        }
        return finalFile.substring(0, idx) +"-"+ UUID.randomUUID().toString() + "." + finalFile.substring(idx+1);
    }
}
