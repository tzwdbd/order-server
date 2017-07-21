package com.oversea.task.enums;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.haitao.task.enums
 * @date 15/7/27 09:47
 */
public enum ExternalProductCode {

    AMAZON("amazon", "001"),
    _6PM("_6pm", "002"),
    FINISHLINE("finishline", "003"),
    AMAZON_JP("amazon.jp", "004"),
    ZAPPOS("zappos", "005"),
    LOOKFANTASTIC("lookfantastic", "006"),
    DRUGSTORE("drugstore", "007"),
    SHOPBOP("shopbop", "008"),
    NISSEN("nissen", "009"),
    GYMBOREE("gymboree", "010"),
    IHERB("iherb", "011"),
    DIAPERS("diapers", "012"),
    CLINIQUE("clinique", "013"),
    ASHFORD("ashford", "014"),
    ORIGINS("origins", "015"),
    BELLEMAISON("belleMaison", "016"),
    WALGREENS("walgreens", "017"),
    ASOS("asos", "018"),
    EASTBAY("eastbay", "019"),
    FAMOUSFOOTWEAR("famousfootwear", "020"),
    GNC("gnc", "021"),
    RALPHLAUREN("ralphlauren", "022"),
    NINEWEST("ninewest", "023"),
    ESTEELAUDER("esteelauder", "024"),
    KATESPADE("katespade", "025"),
    NORDSTROM("nordstrom", "026"),
    DSW("dsw", "027"),
    JOMASHOP("jomashop", "028"),
    KIPLING("kipling", "029"),
    GILT("gilt", "030"),
    VICTORIASSECRET("victoriassecret", "031"),
    FOOTLOCKER("footlocker", "032"),
    SAKS("saks", "033"),
    SKINSTORE("skinstore", "034"),
    CARTERS("carters", "035"),
    OSHKOSH("oshkosh", "036"),
    SHISEIDO("shiseido", "037"),
    LEVIS("levis", "038"),
    VITACOST("vitacost", "039"),
    NAUTICA("nautica", "040"),
    BLOOMINGDALES("bloomingdales", "041"),
    DISNEYSTORE("disneystore", "042"),
    BOBBIBROWNCOSMETICS("bobbibrowncosmetics", "043"),
    SHOES("shoes", "044"),
    GIORGIOARMANIBEAUTY("giorgioarmanibeauty", "045"),
    ROYYOUNGCHEMIST("royyoungchemist", "046"),
    LACOSTE("lacoste", "047"),
    SASA("sasa", "048"),
    BROOKSBROTHERS("brooksbrothers", "049"),
    REI("rei", "050"),
    SWAROVSKI("swarovski", "051"),
    BACKCOUNTRY("backcountry", "052"),
    SHOEBUY("shoebuy", "053"),
    FEELUNIQUE("feelunique", "054"),
    ESCENTUAL("escentual", "055"),
    MANKIND("mankind", "056"),
    NEIMANMARCUS("neimanmarcus", "057"),
    CNROYYOUNGCHEMIST("cnroyyoungchemist", "058"),
    THEHUT("thehut", "059"),
    PHARMACYONLINE("pharmacyonline", "060"),
    YODOBASHI("yodobashi", "061"),
    UNINEED("unineed", "062"),
    AMAZON_UK("amazon.uk", "063"),
    AMAZON_DE("amazon.de", "064"),
    MACYS("macys", "065"),
    hksasa("hksasa", "066"),
    toryburch("toryburch", "067"),
    coggles("coggles", "068"),
    chemistwarehouse("chemistwarehouse", "069"),
    ssense("ssense", "070"),
    josephjoseph("josephjoseph", "071"),
    worldofwatches("worldofwatches", "072"),
    fwrd("fwrd","073"),
    jcrew("jcrew","074"),
    otteny("otteny","075"),
    mybag("mybag","076"),;

    ExternalProductCode(String value, String code) {
        this.value = value;
        this.code = code;
    }

    public static ExternalProductCode getExternalProductCode(String orign, String value) {
        for (ExternalProductCode productCode : ExternalProductCode.values()) {
            if ("amazon".equals(value)) {
                if ("us".equals(orign)) {
                    return AMAZON;
                } else if (productCode.getValue().equals(value + "." + orign)) {
                    return productCode;
                }
            } else if (productCode.getValue().equals(value)) {
                return productCode;
            }
        }
        return null;
    }

    private String value;

    private String code;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
