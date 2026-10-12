package com.bluteki.ussd.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ProtocolTest {

    @Test
    void parsesMovitelStartFromSpecification() {
        String xml = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://viettel.com/xsd">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <urn:start soapenv:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                      <user xsi:type="xsd:string">Movitel</user>
                      <service_code xsi:type="xsd:string">*247#</service_code>
                      <pass xsi:type="xsd:string">Movitel1234</pass>
                      <msisdn xsi:type="xsd:string">866891368</msisdn>
                      <session_id xsi:type="xsd:string">2</session_id>
                      <transactionid xsi:type="xsd:string">W1-2-1395280360802</transactionid>
                      <requestType xsi:type="xsd:string">100</requestType>
                      <ussdgw_id xsi:type="xsd:string">1</ussdgw_id>
                    </urn:start>
                  </soapenv:Body>
                </soapenv:Envelope>""";

        MovitelProtocol.Request r = MovitelProtocol.parse(xml);

        assertThat(r.phase()).isEqualTo(Phase.START);
        assertThat(r.dialedCode()).isEqualTo("*247#");
        assertThat(r.transactionId()).isEqualTo("W1-2-1395280360802");
        assertThat(r.user()).isEqualTo("Movitel");
        assertThat(r.msisdn()).isEqualTo("866891368");
    }

    @Test
    void readsMenuSentByExistingAppsDespiteMalformedXml() {
        // Formato enviado hoje pelo cmm-ussd: atributos colados, prefixo urn por declarar e <br> por fechar.
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + "xmlns:xsd=\"http://viettel.com/xsd\"><soapenv:Header/><soapenv:Body><urn:start><user>u</user><pass>p</pass>"
                + "<msg>CMM<br>1. Pagar<br>2. Sair</msg><session_id>9</session_id><transactionid>T-1</transactionid>"
                + "<requestType>202</requestType><ussdgw_id>1</ussdgw_id></urn:start></soapenv:Body></soapenv:Envelope>";

        MovitelProtocol.Request r = MovitelProtocol.parse(xml);

        assertThat(MovitelProtocol.menuText(r.message())).isEqualTo("CMM\n1. Pagar\n2. Sair");
        assertThat(r.requestType()).isEqualTo(MovitelProtocol.MENU);
    }

    @Test
    void buildsMovitelMenuThatRoundTrips() {
        var request = new MovitelProtocol.Request("u", "p", "258861234567", "*247#", null, "2", "T-9", "100", "1", null);
        String xml = MovitelProtocol.menu(request, "Saldo: 100 MT & bonus\n1. Voltar", true, "partner", "secret");

        MovitelProtocol.Request parsed = MovitelProtocol.parse(xml);
        assertThat(parsed.requestType()).isEqualTo(MovitelProtocol.FINAL);
        assertThat(parsed.transactionId()).isEqualTo("T-9");
        assertThat(MovitelProtocol.menuText(parsed.message())).isEqualTo("Saldo: 100 MT & bonus\n1. Voltar");
        assertThat(MovitelProtocol.ackCode(MovitelProtocol.ack(MovitelProtocol.OK))).isEqualTo("0");
    }

    @Test
    void parsesTruRouteXmlAndParams() {
        var fromXml = TruRouteProtocol.parseXml(
                "<ussd><msisdn>258820253890</msisdn><sessionid>171</sessionid><type>1</type><msg>612*777</msg></ussd>");
        assertThat(fromXml.phase()).isEqualTo(Phase.START);
        assertThat(fromXml.message()).isEqualTo("612*777");

        var fromParams = TruRouteProtocol.fromParams(
                Map.of("msisdn", "258844609303", "sessionid", "4060", "type", "4", "msg", "User timeout"));
        assertThat(fromParams.phase()).isEqualTo(Phase.END);

        String response = TruRouteProtocol.response("258820253890", "171", "Ola <Ana>\n1. Sim", false);
        var reply = TruRouteProtocol.parseResponse(response);
        assertThat(reply.message()).isEqualTo("Ola <Ana>\n1. Sim");
        assertThat(reply.end()).isFalse();

        assertThatThrownBy(() -> TruRouteProtocol.fromParams(Map.of("sessionid", "1", "type", "9")))
                .isInstanceOf(ProtocolException.class);
    }

    @Test
    void handlesFlaresParamsAndResponses() {
        var r = FlaresProtocol.fromParams(Map.of("MSISDN", "258841234567", "SessionId", "abc", "NewRequest", "true",
                "SubscriberInput", "*123#"));
        assertThat(r.phase()).isEqualTo(Phase.START);
        assertThat(FlaresProtocol.response("Menu", false)).isEqualTo("FC Menu");
        assertThat(FlaresProtocol.parseResponse("FB Obrigado").end()).isTrue();
        assertThat(FlaresProtocol.parseResponse("FC 1. Sim").message()).isEqualTo("1. Sim");
    }

    @Test
    void matchesServiceCodesWithSubCodes() {
        assertThat(ServiceCodes.normalize(" *612*777# ")).isEqualTo("612*777");
        assertThat(ServiceCodes.matches("612*777", "*612*777#")).isTrue();
        assertThat(ServiceCodes.matches("*612*777*1#", "*612*777#")).isTrue();
        assertThat(ServiceCodes.matches("*612*7771#", "*612*777#")).isFalse();
        assertThat(ServiceCodes.display("612")).isEqualTo("*612#");
    }

    @Test
    void rejectsExternalEntities() {
        String xxe = "<?xml version=\"1.0\"?><!DOCTYPE x [<!ENTITY e SYSTEM \"file:///etc/passwd\">]>"
                + "<ussd><sessionid>&e;</sessionid><type>1</type></ussd>";
        assertThatThrownBy(() -> TruRouteProtocol.parseXml(xxe)).isInstanceOf(ProtocolException.class);
    }
}
