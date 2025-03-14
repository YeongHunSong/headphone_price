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
                "1\\.94\\.107\\.209"
                + "|8\\.211\\.162\\.45"
                + "|20\\.65\\.192\\.67"
                + "|20\\.221\\.68\\.61"
                + "|20\\.221\\.68\\.102"
                + "|20\\.225\\.3\\.171"
                + "|36\\.189\\.253\\.253"
                + "|39\\.105\\.169\\.60"
                + "|45\\.144\\.65\\.226"
                + "|45\\.148\\.10\\.34"
                + "|45\\.148\\.10\\.90"
                + "|45\\.156\\.129\\.54"
                + "|45\\.237\\.79\\.94"
                + "|46\\.101\\.115\\.174"
                + "|46\\.142\\.124\\.118"
                + "|47\\.251\\.93\\.227"
                + "|48\\.217\\.211\\.242"
                + "|49\\.175\\.89\\.60"
                + "|57\\.152\\.56\\.111"
                + "|62\\.146\\.183\\.54"
                + "|64\\.62\\.156\\.112"
                + "|64\\.227\\.123\\.94"
                + "|68\\.183\\.212\\.134"
                + "|70\\.39\\.75\\.140"
                + "|70\\.39\\.75\\.141"
                + "|70\\.39\\.75\\.178"
                + "|76\\.91\\.8\\.155"
                + "|77\\.253\\.224\\.13"
                + "|78\\.153\\.140\\.90"
                + "|87\\.255\\.194\\.135"
                + "|89\\.168\\.64\\.247"
                + "|91\\.208\\.184\\.85"
                + "|91\\.208\\.206\\.134"
                + "|92\\.255\\.57\\.58"
                + "|103\\.78\\.0\\.161"
                + "|103\\.193\\.190\\.74"
                + "|104\\.234\\.115\\.24"
                + "|107\\.151\\.200\\.253"
                + "|121\\.36\\.96\\.194"
                + "|123\\.60\\.174\\.194"
                + "|128\\.199\\.29\\.224"
                + "|130\\.61\\.37\\.51"
                + "|139\\.59\\.9\\.128"
                + "|141\\.11\\.62\\.62"
                + "|143\\.110\\.153\\.103"
                + "|152\\.32\\.172\\.115"
                + "|157\\.10\\.97\\.180"
                + "|159\\.65\\.125\\.93"
                + "|159\\.89\\.22\\.184"
                + "|159\\.203\\.90\\.125"
                + "|164\\.90\\.226\\.218"
                + "|172\\.203\\.251\\.40"
                + "|180\\.178\\.94\\.73"
                + "|183\\.215\\.90\\.45"
                + "|182\\.215\\.90\\.45"
                + "|185\\.91\\.69\\.5"
                + "|185\\.242\\.226\\.155"
                + "|186\\.216\\.161\\.147"
                + "|195\\.65\\.125\\.93"
                + "|195\\.178\\.110\\.163"
                + "|198\\.74\\.55\\.191"
                + "|201\\.98\\.30\\.238"
                + "|202\\.160\\.23\\.2"
                + "|219\\.153\\.12\\.42"
                + "|182\\.219\\.21\\.198"
                + "|27\\.124\\.133\\.149"
        );

        valve.setDenyStatus(404);
        return valve;
    }
}
