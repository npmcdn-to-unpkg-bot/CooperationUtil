package pub.amitabha.wechat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlResponseText {
	@XmlCDATA
	private String ToUserName;

	@XmlCDATA
	private String FromUserName;

	private long CreateTime;

	@XmlCDATA
	private String MsgType;

	@XmlCDATA
	private String Content;

	public XmlResponseText() {
		CreateTime = java.lang.System.currentTimeMillis();
		MsgType = "text";
	}

	public XmlResponseText(String toUserName, String fromUserName, String content) {
		CreateTime = java.lang.System.currentTimeMillis();
		MsgType = "text";
		setToUserName(toUserName);
		setFromUserName(fromUserName);
		setContent(content);
	}

	@Override
	public String toString() {
		try {
			JAXBContext jc = JAXBContext.newInstance(XmlResponseText.class);
			Marshaller marshaller = jc.createMarshaller();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(this, out);
			return out.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
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

}
