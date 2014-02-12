package stormy.pythian.web.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import stormy.pythian.service.spring.ServiceConfiguration;

@Configuration
@ComponentScan({ "stormy.pythian.web" })
@Import({ ServiceConfiguration.class })
public class WebConfiguration {

}
