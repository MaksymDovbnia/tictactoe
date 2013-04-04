package com.net.online.protobuf;

import com.google.protobuf.AbstractMessageLite;

/**
 * Created with IntelliJ IDEA. User: legioner Date: 08.08.12 Time: 16:48 To
 * change this template use File | Settings | File Templates.
 */
public class ProtoEncoder {

	/**
	 * Р�СЃРїРѕР»СЊР·РѕРІР°РЅРёРµ СЂРѕРґРёС‚РµР»СЊСЃРєРѕРіРѕ РєР»Р°СЃСЃР°
	 * AbstractMessageLite РїРѕР·РІРѕР»СЏРµС‚ РІС‹Р·С‹РІР°С‚СЊ РјРµС‚РѕРґ
	 * toByteArray()
	 * 
	 * @param object
	 * @return
	 */
	public static Envelope encode(AbstractMessageLite object) {
		Envelope result = new Envelope();
		// System.out.println("Update: "+object.toString());
		byte[] data = object.toByteArray();
		result.setData(data);
		result.setLength(data.length);
		result.setType(ProtoType.fromClass(object.getClass()));
		return result;
	}

}
