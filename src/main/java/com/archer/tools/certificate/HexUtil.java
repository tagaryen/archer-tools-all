package com.archer.tools.certificate;



public class HexUtil {
	
	public static final byte DEFAULT_BYTE = 0x7f;
	
	public static final int[] hexToByteTable = new int[128];
	
	public static final char[] byteToHexTable = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
	    
    static {
    	for(int i = 0; i < hexToByteTable.length; ++i) {
			hexToByteTable[i] = DEFAULT_BYTE;
		}
    	for(int i = 0; i < byteToHexTable.length; ++i) {
			hexToByteTable[byteToHexTable[i]] = i;
		}
    }
	
    /**
	 * @param hexStr hex number in string.
	 * @return bytes number in bytes.
	 * */
	public static byte[] hexStrToBytes(String hexStr) {
		if(null == hexStr || hexStr.isEmpty()) {
			return new byte[0];
		}
		String hex = hexStr.toLowerCase();
		if (hex.startsWith("0x")) {
			hex = hex.substring(2);
		}
		if(hex.length()%2 == 1) {
			hex = "0"+hex;
		}
		byte[] out = new byte[hex.length()>>1];
		for(int i = 0; i < hex.length(); i += 2) {
			char c1 = hex.charAt(i), c2 = hex.charAt(i+1);
			if(c1 < 0 || c1 > 128 || c2 < 0 || c2 > 128 || hexToByteTable[c1] == DEFAULT_BYTE || hexToByteTable[c2] == DEFAULT_BYTE) {
				throw new java.lang.IllegalArgumentException("Invalid hex string, "+hexStr);
			}
			out[i>>1] = (byte) ((hexToByteTable[c1]<<4)|hexToByteTable[c2]);
		}
		return out;
	}

    /**
	 * @param bs number in bytes.
	 * @return string hex number in string.
	 * */
	public static String bytesToHexStr(byte[] bs) {
		if(null == bs || bs.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(byte b: bs) {
			int bi = b;
			if(bi < 0) {
				bi = 256+bi;
			}
			int b1 = (bi>>4), b2 = bi&0b1111;
			sb.append(byteToHexTable[b1]);
			sb.append(byteToHexTable[b2]);
		}
		return sb.toString();
	}


	/**
	 * @param num
	 *            一个整型数据
	 * @return 4个字节的自己数组
	 */
	public static byte[] intToBytes(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (0xff & (num >> 0));
		bytes[1] = (byte) (0xff & (num >> 8));
		bytes[2] = (byte) (0xff & (num >> 16));
		bytes[3] = (byte) (0xff & (num >> 24));
		return bytes;
	}

	/**
	 * @param bytes 4个字节的字节数组
	 * @return 一个整型数据
	 */
	public static int byteToInt(byte[] bytes) {
		int num = 0;
		int temp;
		temp = (0x000000ff & (bytes[0])) << 0;
		num = num | temp;
		temp = (0x000000ff & (bytes[1])) << 8;
		num = num | temp;
		temp = (0x000000ff & (bytes[2])) << 16;
		num = num | temp;
		temp = (0x000000ff & (bytes[3])) << 24;
		num = num | temp;
		return num;
	}

	/**
	 * @param num  一个长整型数据
	 * @return 4个字节的自己数组
	 */
	public static byte[] longToBytes(long num) {
		byte[] bytes = new byte[8];
		for (int i = 0; i < 8; i++) {
			bytes[i] = (byte) (0xff & (num >> (i * 8)));
		}

		return bytes;
	}
}
