package lottery.domains.content.payment.af;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javautils.http.HttpClientUtil;
import javax.servlet.ServletContext;
import lottery.domains.content.AbstractPayment;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

@Component
public class AFPayment
  extends AbstractPayment
  implements InitializingBean
{
  @Autowired
  private ServletContext servletContext;
  private static Map<Integer, String> BRANCH_NAMES = new HashMap();
  @Value("${af.daifu.url}")
  private String daifuUrl;
  @Value("${af.daifu.queryurl}")
  private String daifuQueryUrl;
  
  public void afterPropertiesSet()
    throws Exception
  {
    BRANCH_NAMES.put(Integer.valueOf(1), "中国工商银行股份有限公司上海市龙江路支行");
    BRANCH_NAMES.put(Integer.valueOf(2), "中国建设银行北京市分行营业部");
    BRANCH_NAMES.put(Integer.valueOf(3), "中国农业银行股份有限公司忻州和平分理处");
    BRANCH_NAMES.put(Integer.valueOf(4), "招商银行股份有限公司厦门金湖支行");
    BRANCH_NAMES.put(Integer.valueOf(5), "中国银行股份有限公司赣州市客家大道支行");
    BRANCH_NAMES.put(Integer.valueOf(6), "交通银行北京安翔里支行");
    BRANCH_NAMES.put(Integer.valueOf(7), "上海浦东发展银行安亭支行");
    BRANCH_NAMES.put(Integer.valueOf(8), "兴业银行北京安华支行");
    BRANCH_NAMES.put(Integer.valueOf(9), "中信银行北京安贞支行");
    BRANCH_NAMES.put(Integer.valueOf(10), "宁波银行股份有限公司北京东城支行");
    BRANCH_NAMES.put(Integer.valueOf(11), "上海银行股份有限公司北京安贞支行");
    BRANCH_NAMES.put(Integer.valueOf(12), "杭州银行股份有限公司上海北新泾支行");
    BRANCH_NAMES.put(Integer.valueOf(13), "渤海银行股份有限公司北京朝阳门支行");
    BRANCH_NAMES.put(Integer.valueOf(14), "浙商银行股份有限公司杭州滨江支行");
    BRANCH_NAMES.put(Integer.valueOf(15), "广发银行股份有限公司北京朝阳北路支行");
    BRANCH_NAMES.put(Integer.valueOf(16), "中国邮政储蓄银行股份有限公司北京昌平区北七家支行");
    BRANCH_NAMES.put(Integer.valueOf(17), "深圳发展银行");
    BRANCH_NAMES.put(Integer.valueOf(18), "中国民生银行股份有限公北京西大望路支行");
    BRANCH_NAMES.put(Integer.valueOf(19), "中国光大银行股份有限公司北京安定门支行");
    BRANCH_NAMES.put(Integer.valueOf(20), "华夏银行北京德外支行");
    BRANCH_NAMES.put(Integer.valueOf(21), "北京银行安定门支行");
    BRANCH_NAMES.put(Integer.valueOf(22), "南京银行股份有限公司北京车公庄支行");
    BRANCH_NAMES.put(Integer.valueOf(23), "平安银行股份有限公司北京北苑支行");
    BRANCH_NAMES.put(Integer.valueOf(24), "北京农村商业银行股份有限公司漷县支行");
  }
  
  public String daifu(WebJSONObject json, UserWithdraw order, UserCard card, PaymentChannelBank bank, PaymentChannel channel)
  {
    try
    {
      logStart(order, bank, channel);
      return daifuInternal(json, order, card, bank, channel);
    }
    catch (Exception e)
    {
      logException(order, bank, channel, "代付请求失败", e);
      json.set(Integer.valueOf(2), "2-4000");
    }
    return null;
  }
  
  public static String base64Encoder(String ss, String charset)
  {
    BASE64Encoder en = new BASE64Encoder();
    String encStr = "";
    if ((charset == null) || ("".equals(charset)))
    {
      encStr = en.encode(ss.getBytes());
      return encStr;
    }
    try
    {
      encStr = en.encode(ss.getBytes(charset));
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return encStr;
  }
  
  private String daifuInternal(WebJSONObject json, UserWithdraw order, UserCard card, PaymentChannelBank bank, PaymentChannel channel)
  {
    try
    {
      Map<String, String> params = new HashMap();
      String cardName = base64Encoder(order.getCardName(), "UTF-8");
      params.put("merchant_no", channel.getMerCode());
      params.put("order_no", order.getBillno());
      params.put("card_no", order.getCardId());
      params.put("account_name", cardName);
      params.put("bank_branch", base64Encoder((String)BRANCH_NAMES.get(Integer.valueOf(bank.getBankId())), "UTF-8"));
      params.put("cnaps_no", "demo");
      params.put("pay_pwd", channel.getExt1());
      params.put("bank_code", bank.getCode());
      params.put("bank_name", order.getBankName());
      params.put("amount", order.getRecMoney()+"");
      String src = "merchant_no=" + (String)params.get("merchant_no") + "&order_no=" + (String)params.get("order_no") + "&card_no=" + 
        (String)params.get("card_no") + "&account_name=" + (String)params.get("account_name") + "&bank_branch=" + 
        (String)params.get("bank_branch") + "&cnaps_no=" + (String)params.get("cnaps_no") + "&bank_code=" + (String)params.get("bank_code") + 
        "&bank_name=" + (String)params.get("bank_name") + "&amount=" + (String)params.get("amount") + "&pay_pwd=" + (String)params.get("pay_pwd");
      src = src + "&key=" + channel.getMd5Key();
      String sign = DigestUtils.md5Hex(src);
      params.put("sign", sign);
      String retStr = HttpClientUtil.post(this.daifuUrl, params, null, 10000);
      if (StringUtils.isEmpty(retStr))
      {
        logError(order, bank, channel, "接口返回空，可能是请求超时");
        json.set(Integer.valueOf(-1), "-1");
        return null;
      }
      AFDaifuResult result = (AFDaifuResult)JSON.parseObject(retStr, AFDaifuResult.class);
      if (result == null)
      {
        logError(order, bank, channel, "请求失败，解析返回数据失败，返回数据为：" + retStr);
        json.setWithParams(Integer.valueOf(2), "2-4007", new Object[] { StringUtils.abbreviate(retStr, 20) });
        return null;
      }
      if ("000000".equals(result.getResult_code()))
      {
        if (StringUtils.isEmpty(result.getMer_order_no()))
        {
          logError(order, bank, channel, "请求失败，返回第三方注单号为空，返回数据为：" + retStr);
          json.setWithParams(Integer.valueOf(2), "2-4007", new Object[] { StringUtils.abbreviate(retStr, 20) });
          return null;
        }
        logSuccess(order, order.getBillno(), channel);
        return order.getBillno();
      }
      logError(order, bank, channel, "请求返回错误消息，返回数据：" + retStr + "，开始查询订单状态");
      AFDaifuQueryResult queryResult = query(order, channel);
      if (isSuccessRequested(queryResult))
      {
        logSuccess(order, queryResult.getOrder_no(), channel);
        return queryResult.getOrder_no();
      }
      logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
      json.setWithParams(Integer.valueOf(2), "2-4002", new Object[] { result.getResult_msg() });
      return null;
    }
    catch (Exception e)
    {
      logException(order, bank, channel, "代付请求失败", e);
      json.set(Integer.valueOf(-1), "-1");
    }
    return null;
  }
  
  public static void main(String[] args) {}
  
  public AFDaifuQueryResult query(UserWithdraw order, PaymentChannel channel)
  {
    try
    {
      String outOrderId = order.getBillno();
      String merchantCode = channel.getMerCode();
      Map<String, String> params = new HashMap();
      params.put("merchant_no", merchantCode);
      params.put("order_no", outOrderId);
      
      String src = "merchant_no=" + merchantCode + "&order_no=" + outOrderId + "&key=" + channel.getMd5Key();
      String sign = DigestUtils.md5Hex(src);
      params.put("sign", sign);
      String retStr = HttpClientUtil.post(this.daifuQueryUrl, params, null, 10000);
      if (StringUtils.isEmpty(retStr))
      {
        logError(order, null, channel, "查询请求失败，发送请求后返回空数据");
        return null;
      }
      logInfo(order, null, channel, "查询返回数据：" + retStr);
      
      AFDaifuQueryResult result = null;
      try
      {
        result = (AFDaifuQueryResult)JSON.parseObject(retStr, AFDaifuQueryResult.class);
        if (result == null)
        {
          logError(order, null, channel, "查询请求失败，解析返回数据失败");
          return null;
        }
      }
      catch (Exception e)
      {
        logError(order, null, channel, "查询请求失败，解析返回数据失败");
        return null;
      }
      return result;
    }
    catch (Exception e)
    {
      logException(order, null, channel, "查询请求失败", e);
    }
    return null;
  }
  
  public boolean isSuccessRequested(AFDaifuQueryResult queryResult)
  {
    if (queryResult == null) {
      return false;
    }
    if (StringUtils.isEmpty(queryResult.getOrder_no())) {
      return false;
    }
    if ("000000".equalsIgnoreCase(queryResult.getResult_code())) {
      return true;
    }
    if ("S".equalsIgnoreCase(queryResult.getResult())) {
      return true;
    }
    return false;
  }
  
  public int transferBankStatus(String bankStatus)
  {
    int remitStatus = -4;
    String str;
    switch ((str = bankStatus).hashCode())
    {
    case 70: 
      if (str.equals("F")) {}
      break;
    case 72: 
      if (str.equals("H")) {
        break;
      }
      break;
    case 83: 
      if (!str.equals("S"))
      {
        return remitStatus;
      }
      else
      {
        remitStatus = 2;
        return remitStatus;
//        remitStatus = -2;
      }
//      break;
    }
    return remitStatus;
  }
}
