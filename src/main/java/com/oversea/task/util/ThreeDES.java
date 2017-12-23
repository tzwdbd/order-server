package com.oversea.task.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;



@SuppressWarnings("restriction")
public class ThreeDES {

	
	public static final byte[] keybyte = "CbvYYHo2oWGY1cKiytlijweK".getBytes();
    private static final String Algorithm = "DESede";  //定义 加密算法,可用 DES,DESede,Blowfish
    private static Log log = LogFactory.getLog(ThreeDES.class);
    
    @SuppressWarnings("restriction")
	public ThreeDES(){
    	Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }
 
    // 加密字符串
    @SuppressWarnings("restriction")
	public static String encryptMode(byte[] src) {
        try { // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return new sun.misc.BASE64Encoder().encode(c1.doFinal(src));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
 
    // 解密字符串
	public static String decryptMode(String src) {
        try { // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            
            return new String(c1.doFinal(new BASE64Decoder().decodeBuffer(src)));
            
        } catch (java.security.NoSuchAlgorithmException e1) {
            log.error("ERROR IN encryptMode cookie1="+src,e1);
        } catch (javax.crypto.NoSuchPaddingException e2) {
            log.error("ERROR IN encryptMode cookie2="+src,e2);
        } catch (java.lang.Exception e3) {
            log.error("ERROR IN encryptMode cookie3="+src,e3);
        }
        return null;
    }
 
    public static void main(String[] args) throws UnsupportedEncodingException{ // 添加新安全算法,如果用JCE就要把它添加进去

     	String decodeStr=URLDecoder.decode("yWuxBvrAX5Y%3D","UTF-8");
        String srcBytes = decryptMode("mjsAQ4p7ngMq");
        System.out.println("解密后的字符串:" + srcBytes);
        
    	String [] userIdList = {
    			"1962",
    			"1954",
    			"302",
    			"1787",
    			"1812",
    			"1813",
    			"1814",
    			"1815",
    			"1816",
    			"1822",
    			"1823",
    			"1824",
    			"1825",
    			"1826",
    			"1827",
    			"1828",
    			"1829",
    			"1830",
    			"1831",
    			"1832",
    			"1833",
    			"1834",
    			"1835",
    			"1836",
    			"1842",
    			"1843",
    			"1844",
    			"1845",
    			"1846",
    			"1847",
    			"1848",
    			"1849",
    			"1850",
    			"1851",
    			"1852",
    			"1853",
    			"1855",
    			"1912",
    			"1972",
    			"1982",
    			"1983",
    			"1984",
    			"1985",
    			"1986",
    			"1992",
    			"1993",
    			"2003",
    			"2012",
    			"2032",
    			"2052",
    			"2033",
    			"2062",
    			"2072",
    			"2082",
    			"2092",
    			"2102",
    			"2112",
    			"2113",
    			"2114",
    			"2116",
    			"2117",
    			"2132",
    			"2118",
    			"2152",
    			"2153",
    			"2154",
    			"2155",
    			"2156",
    			"2157",
    			"2158",
    			"2159",
    			"2160",
    			"2161",
    			"2162",
    			"2172",
    			"2173",
    			"2174",
    			"2175",
    			"2176",
    			"2177",
    			"2178",
    			"2182",
    			"2179",
    			"2180",
    			"2181",
    			"2192",
    			"2193",
    			"2202",
    			"2194",
    			"2195",
    			"2196",
    			"2197",
    			"2198",
    			"2199",
    			"2200",
    			"2201",
    			"2222",
    			"2223",
    			"2224",
    			"2225",
    			"2226",
    			"2227",
    			"2232",
    			"2212",
    			"2233",
    			"2242",
    			"2234",
    			"2235",
    			"2236",
    			"2237",
    			"2238",
    			"2239",
    			"2252",
    			"2253",
    			"2262",
    			"2272",
    			"2273",
    			"2274",
    			"2275",
    			"2276",
    			"2277",
    			"2278",
    			"2279",
    			"2282",
    			"2292",
    			"2302",
    			"2303",
    			"2304",
    			"2305",
    			"2306",
    			"2307",
    			"2042",
    			"2308",
    			"2309",
    			"2310",
    			"2311",
    			"2312",
    			"2313",
    			"2314",
    			"2315",
    			"2316",
    			"2322",
    			"2323",
    			"2324",
    			"2332",
    			"2333",
    			"2334",
    			"2335",
    			"2336",
    			"2337",
    			"2338",
    			"2339",
    			"2340",
    			"2341",
    			"2342",
    			"2343",
    			"2352",
    			"2362",
    			"2372",
    			"2373",
    			"2374",
    			"2375",
    			"2376",
    			"2377",
    			"2378",
    			"2382",
    			"2392",
    			"2402",
    			"2412",
    			"2413"
    	};
    	
    	for (int i = 0; i < userIdList.length;i++){
    		String cookie =  encryptMode(userIdList[i].getBytes());
            System.out.println("\""+cookie+"\",");
    	}
    	
    }
	
}
