package org.han.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * @author HanZhou
 * @className: ISimpleDao
 * @date 2012-8-14
 * @extends Object
 * @description: 底层DAO封装接口类
 */
public interface ISimpleDao<T, PK extends Serializable> {
	/**
	 * 取得当前Session.
	 */
	public Session getSession();

	/**
	 * 保存新增或修改的对象.
	 */
	public void save(final T entity);

	/**
	 * 保存新增的对象,返回该对象.
	 */
	public T saveNew(final T entity);

	/**
	 * 删除对象.
	 * @param entity
	 *            对象必须是session中的对象或含id属性的transient对象.
	 */
	public void delete(final T entity);
	
	/**
	 * 按id删除对象.
	 */
	public void delete(final PK id);

	/**
	 * 按id获取对象.
	 */
	public T get(final PK id);

	/**
	 * 按类和id获取对象
	 */
	public <X> X get(final Class<X> clazz, final Serializable id);
	
	/**
	 * 按HQL查询对象列表.
	 */
	public <X> List<X> find(final String hql);
	
	/**
	 * 按HQL查询对象列表.
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 */
	public <X> List<X> find(final String hql, final Object... values);
	
	/**
	 * 按HQL查询对象列表.
	 * @param values
	 *            命名参数,按名称绑定.
	 */
	public <X> List<X> find(final String hql,final Map<String, Object> values);
	
	/**
	 * 按HQL查询唯一对象.
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 */
	public <X> X findUnique(final String hql, final Object... values);
	
	/**
	 * 按HQL查询唯一对象.
	 * @param values
	 *            命名参数,按名称绑定.
	 */
	public <X> X findUnique(final String hql,final Map<String, Object> values);
	
	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 */
	public Query createQuery(final String hql, final Object... values);
	
	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * @param values
	 *            命名参数,按名称绑定.
	 */
	public Query createQuery(final String hql,final Map<String, Object> values);
	
	/**
	 * 根据查询SQL与参数列表创建SQLQuery对象.
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 */
	public SQLQuery createSQLQuery(final String sql,final Object... values);
	
	/**
	 * 根据查询SQL与参数列表创建SQLQuery对象.
	 * @param values
	 *            命名参数,按名称绑定.
	 */
	public SQLQuery createSQLQuery(final String sql,final Map<String, Object> values);
}
