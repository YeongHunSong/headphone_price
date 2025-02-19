package hpPrice;

import org.apache.catalina.valves.RemoteAddrValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class IpBlockTomcatCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    // remoteAddrValve 의 경우 서버 단에서 바로 차단. 애플리케이션까지 넘어오지 않음.
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextValves(remoteAddrValve());
    }

    private RemoteAddrValve remoteAddrValve() {
        RemoteAddrValve valve = new RemoteAddrValve();
        valve.setAllow(".*");

        valve.setDeny(
                "45\\.148\\.10\\.90"
                + "|57\\.152\\.56\\.111"
                + "|195\\.178\\.110\\.163"
                + "|77\\.253\\.224\\.13"
                + "|92\\.255\\.57\\.58"
                + "|47\\.251\\.93\\.227"
                + "|185\\.91\\.69\\.5"
                + "|192\\.168\\.0\\.2"
        );

        valve.setDenyStatus(404);
        return valve;
    }
}
