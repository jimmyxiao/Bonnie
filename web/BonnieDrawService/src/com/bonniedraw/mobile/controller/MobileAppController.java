package com.bonniedraw.mobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/service")
public class MobileAppController {
	
//	@Autowired
//	JavaMailSender mailSender;

//	private boolean isLogin(AppLoginInput appLoginInput){
//		if(ValidateUtil.isNotNumNone(appLoginInput.getUi()) 
//				&& ValidateUtil.isNotBlank(appLoginInput.getLk())
//				&& ValidateUtil.isNotEmpty(appLoginInput.getDt()) ){
//			return true;
//		}else{
//			return false;
//		}
//	}
//	
//	@RequestMapping(value="/usr/login", produces="application/json")
//	public @ResponseBody AppLoginOutput appLogin(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppLoginInput appLoginInput) {
//		String ipAddress = request.getHeader("X-FORWARDED-FOR");
//		resp.setHeader("Access-Control-Allow-Origin", "*");
//		AppLoginOutput result = loginService.appLogin(appLoginInput, ipAddress);
//		return result;
//	}
//	
//	@RequestMapping(value="/theme/index_info", produces="application/json")
//	public @ResponseBody ResIndexInfoVO queryIndexInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppInputBaseModel appInput) {
//		ResIndexInfoVO result  = new ResIndexInfoVO();
//		ExpoTheme expoTheme = expoThemeService.queryAppTheme();
//		if(expoTheme!=null){
//			result.setExpoThemeName(expoTheme.getExpoThemeName());
//			result.setIndexThemeDesc(expoTheme.getIndexThemeDesc());
//			result.setIndexThemeImage(expoTheme.getIndexThemeImage());
//			result.setIndexHallDesc(expoTheme.getIndexHallDesc());
//			result.setIndexHallImage(expoTheme.getIndexHallImage());
//			result.setIndexBoothDesc(expoTheme.getIndexBoothDesc());
//			result.setIndexBoothImage(expoTheme.getIndexBoothImage());
//			result.setIndexSpeechDesc(expoTheme.getIndexSpeechDesc());
//			result.setIndexSpeechImage(expoTheme.getIndexSpeechImage());
//			result.setIndexPartnerDesc(expoTheme.getIndexPartnerDesc());
//			result.setIndexPartnerImage(expoTheme.getIndexPartnerImage());
//			result.setIndexNewName(expoTheme.getIndexNewName());
//			result.setIndexNewDesc(expoTheme.getIndexNewDesc());
//			result.setIndexNewImage(expoTheme.getIndexNewImage());
//			result.setLinkFb(expoTheme.getLinkFb());
//			result.setLinkYoutube(expoTheme.getLinkYoutube());
//			result.setLinkLine(expoTheme.getLinkLine());
//			result.setLinkInstagram(expoTheme.getLinkInstagram());
//			result.setLinkTwitter(expoTheme.getLinkTwitter());
//			result.setRes(1);
//		}
//		return result;
//	}
//	
//	@RequestMapping(value="/theme/theme_info", produces="application/json")
//	public @ResponseBody ResThemeInfoVO queryThemeInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppInputBaseModel appInput) {
//		ResThemeInfoVO result  = new ResThemeInfoVO();
//		ExpoTheme expoTheme = expoThemeService.queryExpoTheme();
//		if(expoTheme!=null){
//			result.setExpoThemeName(expoTheme.getExpoThemeName());
//			result.setExpoThemeSlogan(expoTheme.getExpoThemeSlogan());
//			result.setStartDay(expoTheme.getStartDay());
//			result.setEndDay(expoTheme.getEndDay());
//			result.setRelationUrl(expoTheme.getRelationUrl());
//			result.setShareLink(expoTheme.getShareLink());
//			result.setImagePath(expoTheme.getImagePath());
//			result.setVideoLink(expoTheme.getVideoLink());
//			result.setCopyright(expoTheme.getCopyright());
//			result.setOrganizer(expoTheme.getOrganizer());
//			result.setOrganizerImagePath(expoTheme.getOrganizerImagePath());
//			result.setSponsor(expoTheme.getSponsor());
//			result.setSponsorImagePath(expoTheme.getSponsorImagePath());
//			result.setImplementer(expoTheme.getImplementer());
//			result.setImplementerImagePath(expoTheme.getImplementerImagePath());
//			result.setTel(expoTheme.getTel());				
//			result.setContentList(expoTheme.getContentList());
//			result.setImageList(expoTheme.getImageContentList());
//			result.setRes(1);
//		}
//		return result;
//	}
//	
//	@RequestMapping(value="/hall/hall_info", produces="application/json")
//	public @ResponseBody ResHallInfoVO queryHallInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppHallInput appHallInput) {
//		ResHallInfoVO result = new ResHallInfoVO();
//		List<ExpoHall> expoHallList = expoHallService.queryExpoHallListForMobile(appHallInput);
//		if(ValidateUtil.isNotEmptyAndSize(expoHallList)){
//			AppStandardInput appStandardInput = new AppStandardInput();
//			appStandardInput.setStype(3);
//			for(ExpoHall expoHall:expoHallList){
//				appStandardInput.setHallid(expoHall.getExpoHallId());
//				expoHall.setBoothList(standardService.queryStandardListForMobile(appStandardInput));
//			}
//		}
//		result.setExpoHallList(expoHallList);
//		result.setRes(1);
//		return result;
//	}
//	
//	@RequestMapping(value="/booth", produces="application/json")
//	public @ResponseBody ResHallInfoVO queryBoothInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppHallInput appHallInput) {
//		ResHallInfoVO result = new ResHallInfoVO();
//		List<ExpoHall> expoHallList = standardService.queryBoothInfoForMobile(appHallInput);
//		result.setExpoHallList(expoHallList);
//		result.setRes(1);
//		return result;
//	}
//	
//	@RequestMapping(value="/standard_info", produces="application/json")
//	public @ResponseBody ResStandardInfoVO queryStandardInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppStandardInput appStandardInput) {
//		ResStandardInfoVO result = new ResStandardInfoVO();
//		List<Standard> standardList = standardService.queryStandardListForMobile(appStandardInput);
//		result.setStandardList(standardList);
//		result.setRes(1);
//		return result;
//	}
//	
//	@RequestMapping(value="/collection_set", produces="application/json")
//	public @ResponseBody ResCollectionList setUserCollectionInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppCollectionSetInput appCollectionSetInput) {
//		ResCollectionList result  = new ResCollectionList();
//		AppLoginInput appLoginInput = new AppLoginInput();
//		appLoginInput.setUi(appCollectionSetInput.getUi());
//		appLoginInput.setLk(appCollectionSetInput.getLk());
//		appLoginInput.setDt(appCollectionSetInput.getDt());
//		appLoginInput.setSid(appCollectionSetInput.getSid());
//		appLoginInput.setStype(appCollectionSetInput.getStype());
//		if(isLogin(appLoginInput)){
//			Integer operate = appCollectionSetInput.getOp();
//			if(appCollectionSetInput.getUi() !=null && appCollectionSetInput.getSid() !=null && appCollectionSetInput.getStype()!=null && operate !=null){
//				if(operate ==1 || operate ==2){
//					if(operate ==1){
//						int success = collectionService.insertCollectionInfo(appCollectionSetInput);
//						if(success ==1){
//							result.setRes(1);
//							result.setMsg("加入成功");
//						}else if(success == 2){
//							result.setRes(2);
//							result.setMsg("已收藏此項目");
//						}else{
//							result.setRes(2);
//							result.setMsg("加入失敗");
//						}
//					}else if(operate ==2){
//						int success = collectionService.removeCollectionInfo(appCollectionSetInput);
//						if(success ==1){
//							result.setRes(1);
//							result.setMsg("移除成功");
//						}else if(success == 2){
//							result.setRes(2);
//							result.setMsg("無可移除收藏項目");
//						}else{
//							result.setRes(2);
//							result.setMsg("移除失敗");
//						}
//					}
//					List<ResCollectionVO> userCollectionList = collectionService.queryUesrCollectionForMobile(appCollectionSetInput);
//					result.setResultList(userCollectionList);
//				}else{
//					result.setRes(2);
//					result.setMsg("無此操作");
//				}
//			}else{
//				result.setRes(2);
//				result.setMsg("資訊遺失");
//			}
//		}else{
//			result.setRes(4);
//			result.setMsg("帳號未登入");
//		}
//		return result;
//	}
//	
//	@RequestMapping(value="/collection", produces="application/json")
//	public @ResponseBody ResCollectionList queryCollectionInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppLoginInput appInput) {
//		ResCollectionList result  = new ResCollectionList();
//		if(isLogin(appInput)){
//			List<ResCollectionVO> userCollectionList = collectionService.queryUesrCollectionForMobile(appInput);
//			result.setRes(1);
//			result.setResultList(userCollectionList);
//		}else{
//			result.setRes(4);
//			result.setMsg("帳號未登入");
//		}
//		return result;
//	}
//	
//	@RequestMapping(value="/ticket", produces="application/json")
//	public @ResponseBody ResTicketVO queryTicketInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppInputBaseModel appInput) {
//		ResTicketVO result  = new ResTicketVO();
//		List<Ticket> ticketList = ticketService.queryTicketInfoListForMobile();
//		result.setTicketList(ticketList);
//		result.setRes(1);
//		return result;
//	}
//	
//	@RequestMapping(value="/traffic", produces="application/json")
//	public @ResponseBody ResTrafficInfoList queryTrafficInfo(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody AppInputBaseModel appInput) {
//		ResTrafficInfoList result = new ResTrafficInfoList(); 
//		List<ResTrafficInfoVO> resTrafficInfoVOList = new ArrayList<ResTrafficInfoVO>();
//		Integer id = appInput.getSid();
//		Integer type = appInput.getStype();
//		if(type == null || !(type ==2 || type ==4 || type ==8)){
//			result.setRes(2);
//			result.setMsg("無查詢類別");
//		}else{
//			if(id == null || id ==0){
//				if(type==4 || type ==8){
//					resTrafficInfoVOList = trafficService.queryTrafficByStandardList(type);
//				}else{
//					resTrafficInfoVOList = trafficService.queryTrafficByExpoHallList();
//				}
//			}else{
//				if(type==4 || type ==8){
//					resTrafficInfoVOList  = trafficService.queryTrafficByStandard(id, type);
//				}else{
//					resTrafficInfoVOList = trafficService.queryTrafficByExpoHall(id, type);
//				}
//			}
//			result.setRes(1);
//			result.setResultList(resTrafficInfoVOList);
//		}
//		return result;
//	}
//	
//	@RequestMapping(value="/forgetPwd", produces="application/json",method = RequestMethod.POST)
//	public @ResponseBody AppBaseModel getPwdBySendEmail(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody UserInfo userInput) {
//		AppBaseModel result = new AppBaseModel();
//		result.setRes(0);
//		try {
//			String email = userInput.getEmail();
//			if(ValidateUtil.isBlank(email)){
//				result.setMsg("無郵件資訊");
//				return result;
//			}
//			String userPwd = userInfoService.getPwdByEmail(email);
//			if(ValidateUtil.isBlank(userPwd)){
//				result.setMsg("密碼重設失敗");
//				return result;
//			}
//			
//			SystemSetup systemSetup = systemSetupService.querySystemSetup();
//			if(systemSetup!=null 
//					&& systemSetup.getMailHost()!=null && systemSetup.getMailPort()!=null 
//					&& systemSetup.getMailUsername()!=null && systemSetup.getMailPassword()!=null ){
//				JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//				Properties mailProperties = new Properties();
//				if(ValidateUtil.isNotBlank(systemSetup.getMailProtocol())){
//					mailProperties.put("mail.transport.protocol", systemSetup.getMailProtocol());
//				}
//		        mailProperties.put("mail.smtp.auth", true);
//		        mailProperties.put("mail.smtp.starttls.enable", true);
//		        mailProperties.put("mail.smtp.debug", true);
//		        mailProperties.put("mail.from.email", systemSetup.getMailUsername());
//		        mailSender.setJavaMailProperties(mailProperties);
//		        mailSender.setHost(systemSetup.getMailHost());
//		        mailSender.setPort(systemSetup.getMailPort());
//		        mailSender.setUsername(systemSetup.getMailUsername());
//		        mailSender.setPassword(systemSetup.getMailPassword());
//		        
//		        MimeMessage mimeMessage = mailSender.createMimeMessage();
//				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "BIG5");
//				String to = email;
//				String subject="Tainan Event 找回密碼";
//				String body= "您好！您的密碼為 " 
//						+ userPwd 
//						+" ，請至下方連結重新登入Tainan Event。<br><br>"
//						+"<a href=\"http://104.199.167.141/TainanEventBackend/#/login\">登入Tainan Event</a><br><br>"
//						+"如非本人請忽略本信件，謝謝";
//				message.setTo(to);
//				message.setSubject(subject);
//				message.setFrom(new InternetAddress(systemSetup.getMailUsername()));
//				mimeMessage.setContent(body,"text/html; charset=utf-8");
//		        
//				mailSender.send(mimeMessage);
//				result.setRes(1);
//				result.setMsg("已發送");
//			}else{
//				result.setMsg("發送郵件伺服設定異常");
//			}
//		}catch (Exception e) {
//			LogUtils.error(this.getClass(), "send mail has error : " + e);
//			result.setMsg("發送失敗");
//		}
//		return result;
//	}
	
}
