/**
 * Created 15. nov.. 2007 13.00.53 by Steinar Overbeck Cook
 */
package org.usemon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.DeputoService;
import org.usemon.domain.DimensionalQueryService;
import org.usemon.domain.InvocationService;
import org.usemon.domain.UsemonServiceProvider;
import org.usemon.service.guice.DefaultModuleBinder;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * a Guice based implementation.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 * 
 * @see http://code.google.com/p/google-guice/
 */
public class UsemonServiceProviderImpl implements UsemonServiceProvider {
	private Injector injector = null;
	Logger log = LoggerFactory.getLogger(UsemonServiceProviderImpl.class);
	
	public UsemonServiceProviderImpl() {
		injector = Guice.createInjector(new DefaultModuleBinder());
	}
	
	

	/**
	 * Provides an instance of the {@link DeputoService}
	 */
	public DeputoService deputoService() {
		if (log.isDebugEnabled()) {
			log.debug("Creating deputoService");
		}
		return injector.getInstance(DeputoService.class);
	}


	/**
	 * Provides an instance of the {@link InvocationService}
	 */
	public InvocationService invocationService() {
		InvocationService invocationService = injector.getInstance(InvocationService.class);
		return invocationService;
	}



	/* (non-Javadoc)
	 * @see org.usemon.domain.UsemonServiceProvider#dimensionalQueryService()
	 */
	public DimensionalQueryService dimensionalQueryService() {
		DimensionalQueryService dimensionalQueryService = injector.getInstance(DimensionalQueryService.class);
		return dimensionalQueryService;
	}
}

