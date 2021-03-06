package lottery.domains.content;

import admin.web.WebJSONObject;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPayment
{
  private final Logger log = LoggerFactory.getLogger(getClass());
  
  public abstract String daifu(WebJSONObject paramWebJSONObject, UserWithdraw paramUserWithdraw, UserCard paramUserCard, PaymentChannelBank paramPaymentChannelBank, PaymentChannel paramPaymentChannel);
  
  protected void logStart(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel)
  {
    String bankCode = bank == null ? "" : bank.getCode();
    this.log.info("开始[{}]代付，注单ID：{}，银行名称：{}，银行代码: {}, 商户号：{}", new Object[] { channel.getName(), order.getBillno(), order.getBankName(), bankCode, channel.getMerCode() });
  }
  
  protected void logSuccess(UserWithdraw order, String payOrderId, PaymentChannel channel)
  {
    this.log.info("[{}]代付请求成功，我方注单ID:{}，第三方返回注单ID：{}，商户号：{}", new Object[] { channel.getName(), order.getBillno(), payOrderId, channel.getMerCode() });
  }
  
  protected void logException(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg, Exception e)
  {
    String bankCode = bank == null ? "" : bank.getCode();
    this.log.error("[{}]代付发生异常：{}，注单ID:{}，商户号：{}", new Object[] { channel.getName(), msg, order.getBillno(), order.getBankName(), bankCode, channel.getMerCode(), e });
  }
  
  protected void logException(PaymentChannel channel, String msg, Exception e)
  {
    this.log.error("[{}]发生异常：{}，商户号：{}", new Object[] { channel.getName(), msg, channel.getMerCode(), e });
  }
  
  protected void logInfo(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg)
  {
    String bankCode = bank == null ? "" : bank.getCode();
    this.log.info("[{}]代付{}，注单ID:{}，商户号：{}", new Object[] { channel.getName(), msg, order.getBillno(), order.getBankName(), bankCode, channel.getMerCode() });
  }
  
  protected void logInfo(PaymentChannel channel, String msg)
  {
    this.log.info("[{}]{}，商户号：{}", new Object[] { channel.getName(), msg, channel.getMerCode() });
  }
  
  protected void logWarn(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg)
  {
    String bankCode = bank == null ? "" : bank.getCode();
    this.log.warn("[{}]代付{}，注单ID:{}，商户号：{}", new Object[] { channel.getName(), msg, order.getBillno(), order.getBankName(), bankCode, channel.getMerCode() });
  }
  
  protected void logWarn(PaymentChannel channel, String msg)
  {
    this.log.warn("[{}]{}，商户号：{}", new Object[] { channel.getName(), msg, channel.getMerCode() });
  }
  
  protected void logError(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg)
  {
    this.log.error("[{}]代付{}，注单ID:{}，商户号：{}", new Object[] { channel.getName(), msg, order.getBillno(), channel.getMerCode() });
  }
  
  protected void logError(PaymentChannel channel, String msg)
  {
    this.log.error("[{}]{}，商户号：{}", new Object[] { channel.getName(), msg, channel.getMerCode() });
  }
}
