/**
 * Created 15. nov.. 2007 14.20.11 by Steinar Overbeck Cook
 */
package org.usemon.service.guice;

import javax.sql.DataSource;

import org.usemon.domain.DeputoService;
import org.usemon.domain.DimensionalQueryService;
import org.usemon.domain.InvocationService;
import org.usemon.service.DeputoServiceImpl;
import org.usemon.service.DimensionCacheManager;
import org.usemon.service.DimensionCacheManagerImpl;
import org.usemon.service.DimensionalQueryServiceImpl;
import org.usemon.service.InvocationServiceImpl;
import org.usemon.service.dao.DimensionalDao;
import org.usemon.service.dao.DimensionalDaoImpl;
import org.usemon.service.dao.HeapObservationDao;
import org.usemon.service.dao.HeapObservationDaoImpl;
import org.usemon.service.dao.InvocationObservationDao;
import org.usemon.service.dao.InvocationObservationDaoImpl;
import org.usemon.service.dao.MethodObservationDao;
import org.usemon.service.dao.MethodObservationDaoImpl;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Provides the bindings between interfaces and actual implementations etc.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 */
public class DefaultModuleBinder implements Module {

	public void configure(Binder binder) {
		binder.bind(InvocationService.class).to(InvocationServiceImpl.class).in(Scopes.SINGLETON);
		binder.bind(DeputoService.class).to(DeputoServiceImpl.class).in(Scopes.SINGLETON);
		binder.bind(DataSource.class).toProvider(DataSourceProvider.class);
		binder.bind(DimensionalDao.class).to(DimensionalDaoImpl.class);
		binder.bind(DimensionCacheManager.class).to(DimensionCacheManagerImpl.class).in(Scopes.SINGLETON);
		binder.bind(DimensionalQueryService.class).to(DimensionalQueryServiceImpl.class).in(Scopes.SINGLETON);
		binder.bind(MethodObservationDao.class).to(MethodObservationDaoImpl.class).in(Scopes.SINGLETON);
		binder.bind(InvocationObservationDao.class).to(InvocationObservationDaoImpl.class).in(Scopes.SINGLETON);
		binder.bind(HeapObservationDao.class).to(HeapObservationDaoImpl.class).in(Scopes.SINGLETON);
	}
}
