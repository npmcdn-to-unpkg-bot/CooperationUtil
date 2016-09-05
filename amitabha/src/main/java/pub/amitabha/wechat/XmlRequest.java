package pub.amitabha.wechat;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlRequest {
	@XmlElement(name = "ToUserName")
	private String ToUserName;
	@XmlElement(name = "FromUserName")
	private String FromUserName;
	@XmlElement(name = "CreateTime")
	private long CreateTime;
	@XmlElement(name = "MsgType")
	private String MsgType;
	@XmlElement(name = "Content")
	private String Content;
	@XmlElement(name = "MsgId")
	private String MsgId;

	public static XmlRequest fromXmlString(String xmlContent) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(XmlRequest.class);
		Unmarshaller um = jc.createUnmarshaller();
		StringReader sr = new StringReader(xmlContent);
		InputSource is = new InputSource(sr);
		return (XmlRequest) um.unmarshal(is);
	}

	public boolean isText() {
		return MsgType.trim().equals("text");
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getMsgId() {
		return MsgId;
	}

	public void setMsgId(String msgId) {
		MsgId = msgId;
	}
}
