package pub.amitabha;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.catalina.connector.Connector;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.OrderedCharacterEncodingFilter;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import pub.amitabha.aop.SettingSecurityInterceptor;
import pub.amitabha.aop.UserSecurityInterceptor;
import pub.amitabha.domain.AutoNumber;
import pub.amitabha.domain.AutoNumberRepository;
import pub.amitabha.domain.DummyUser;
import pub.amitabha.domain.DummyUserRepository;
import pub.amitabha.domain.SessionUserHousekeeping;
import pub.amitabha.domain.SessionUserRepository;
import pub.amitabha.domain.User;
import pub.amitabha.domain.UserRepository;
import pub.amitabha.wechat.domain.MyAuthorizationCodeHousekeeping;
import pub.amitabha.wechat.domain.MyAuthorizationCodeRepository;

@SuppressWarnings("unused")
@SpringBootApplication
@EnableWebMvc
@ImportResource({ "classpath:spring-config.xml" })
@PropertySource("classpath:/tomcat.https.properties")
@EnableConfigurationProperties(Application.TomcatSslConnectorProperties.class)
public class Application extends SpringBootServletInitializer {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(this.getClass());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*
	 * Housekeeping parts.
	 */
	@Autowired
	MyAuthorizationCodeRepository repoAut; // For MyAuthorizationCode
											// housekeeping
	@Autowired
	SessionUserRepository repoSess; // For MyAuthorizationCode housekeeping
	@Autowired
	AutoNumberRepository repoAnum; // AutoNumber initialization
	@Autowired
	DummyUserRepository repoDu;

	@Bean
	public CommandLineRunner scheduledTasks(TaskScheduler taskScheduler) {
		return (args) -> {
			// Initialize the AutoNumber class.
			AutoNumber.repo = repoAnum;

			// For MyAuthorizationCode housekeeping
			taskScheduler.scheduleWithFixedDelay(new MyAuthorizationCodeHousekeeping(repoAut),
					MyAuthorizationCodeHousekeeping.MILLISECONDS_TO_KEEP);
			taskScheduler.scheduleWithFixedDelay(new SessionUserHousekeeping(repoSess),
					SessionUserHousekeeping.HOUSEKEEP_FREQUENCY);
		};
	}

	@Bean
	WebMvcConfigurerAdapter WebMvcConfig() {
		return new WebMvcConfigurerAdapter() {
			@Autowired
			private ApplicationContext context;

			/**
			 * 配置拦截器, 一个拦截器不要 add 两次作不同URL mapping, 否则会调用两次，可以反复addPathPatterns
			 * 
			 * @author lance
			 * @param registry
			 */
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new UserSecurityInterceptor(context)).addPathPatterns("/admin/**", "/user/**")
						.excludePathPatterns("/admin/login").excludePathPatterns("/user/login")
						.excludePathPatterns("/user/reg");
				
				//For wechat settings
				registry.addInterceptor(new SettingSecurityInterceptor(context)).addPathPatterns("/wechat/setting/**", "/wechat/setting");
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/**").addResourceLocations("classpath:/public/")
						.addResourceLocations("classpath:/META-INF/resources/")
						.addResourceLocations("classpath:/resources/").addResourceLocations("classpath:/static/");

				super.addResourceHandlers(registry);
			}
		};
	}

	@ConfigurationProperties(prefix = "custom.tomcat.https")
	public static class TomcatSslConnectorProperties {
	    private Integer port;
	    private Boolean ssl = true;
	    private Boolean secure = true;
	    private String scheme = "https";
	    private File keystore;

		private String keystorePassword;
	    //这里为了节省空间，省略了getters和setters，读者在实践的时候要加上

	    public void configureConnector(Connector connector) {
	        if (port != null) {
	            connector.setPort(port);
	        }
	        if (secure != null) {
	            connector.setSecure(secure);
	        }
	        if (scheme != null) {
	            connector.setScheme(scheme);
	        }
	        if (ssl != null) {
	            connector.setProperty("SSLEnabled", ssl.toString());
	        }
	        if (keystore != null && keystore.exists()) {
	            connector.setProperty("keystoreFile", keystore.getAbsolutePath());
	            connector.setProperty("keystorePassword", keystorePassword);
	            //For different version of tomcat, may use keystorePassword or keystorePass. So set them both
	            connector.setProperty("keystorePass", keystorePassword);
	        }
	    }
	    public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public Boolean getSsl() {
			return ssl;
		}

		public void setSsl(Boolean ssl) {
			this.ssl = ssl;
		}

		public Boolean getSecure() {
			return secure;
		}

		public void setSecure(Boolean secure) {
			this.secure = secure;
		}

		public String getScheme() {
			return scheme;
		}

		public void setScheme(String scheme) {
			this.scheme = scheme;
		}

		public File getKeystore() {
			return keystore;
		}

		public void setKeystore(File keystore) {
			this.keystore = keystore;
		}

		public String getKeystorePassword() {
			return keystorePassword;
		}

		public void setKeystorePassword(String keystorePassword) {
			this.keystorePassword = keystorePassword;
		}
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer(TomcatSslConnectorProperties properties) {
	    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
	    tomcat.addAdditionalTomcatConnectors(createSslConnector(properties));
	    return tomcat;
	}

	private Connector createSslConnector(TomcatSslConnectorProperties properties) {
	    Connector connector = new Connector();
	    properties.configureConnector(connector);
	    return connector;
	}
}

/*
 * Codes for reference. No need because you can set it at application.properties
 * 
 * @Bean public CharacterEncodingFilter characterEncodingFilter() { final
 * CharacterEncodingFilter characterEncodingFilter = new
 * CharacterEncodingFilter(); characterEncodingFilter.setEncoding("UTF-8");
 * characterEncodingFilter.setForceEncoding(true); return
 * characterEncodingFilter; }
 * 
 * @Bean FilterRegistrationBean hey(CharacterEncodingFilter encodingFilter){
 * FilterRegistrationBean registry = new FilterRegistrationBean();
 * encodingFilter.setEncoding("UTF8"); encodingFilter.setForceEncoding(true);
 * registry.setFilter(encodingFilter); registry.addUrlPatterns("/**");
 * 
 * StringHttpMessageConverter hey =new StringHttpMessageConverter();
 * hey.setSupportedMediaTypes(MediaType.parseMediaTypes(
 * "text/html;charset=utf-8")); return registry; }
 */