package com.badou.mworking.model;


/**
 * 类:  <code> MainBanner </code>
 * 功能描述: 主页banner
 * 创建人: dongqi
 * 创建日期: 2014年8月8日 下午3:07:32
 * 开发环境: JDK6.0
 */
public class MainBanner {
	
	public static final String CHK_URL = "url";
	public static final String CHK_RES_MD5 = "md5";
	public static final String CHK_IS_NEW = "new";

	private String bannerImgURL;      // banner的图片url地址      
	private String bannerContentURL;  //点击banner进入的url地址
	private String bannerMD5;        //banner的MD5值
	
	public MainBanner(String bannerImgURL, String bannerContentURL,
			String bannerMD5) {
		super();
		this.bannerImgURL = bannerImgURL;
		this.bannerContentURL = bannerContentURL;
		this.bannerMD5 = bannerMD5;
	}
	
	public String bannerToString(String bannerImgURL, String bannerContentURL,
			String bannerMD5){
		return bannerImgURL+"@"+bannerContentURL+"@"+bannerMD5+"@,";
	}
	
	
	/**
	 * @return the bannerImgURL
	 */
	public String getBannerImgURL() {
		return bannerImgURL;
	}
	/**
	 * @param  要设置的 bannerImgURL
	 */
	public void setBannerImgURL(String bannerImgURL) {
		this.bannerImgURL = bannerImgURL;
	}
	/**
	 * @return the bannerContentURL
	 */
	public String getBannerContentURL() {
		return bannerContentURL;
	}
	/**
	 * @param  要设置的 bannerContentURL
	 */
	public void setBannerContentURL(String bannerContentURL) {
		this.bannerContentURL = bannerContentURL;
	}
	/**
	 * @return the bannerMD5
	 */
	public String getBannerMD5() {
		return bannerMD5;
	}
	/**
	 * @param  要设置的 bannerMD5
	 */
	public void setBannerMD5(String bannerMD5) {
		this.bannerMD5 = bannerMD5;
	}
}
