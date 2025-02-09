package org.han.support.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.han.support.IBaseDAO;
import org.han.vo.BaseVo;
import org.han.vo.Page;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

/**
 * @author tender
 * @date 2012-08-14
 * @class BaseDAO
 * @extends SimpleDAOT
 * @description 基础DAO,扩展功能包括分页查询,按属性过滤条件列表查询.
 */
@SuppressWarnings("unchecked")
public class BaseDaoImpl<T, PK extends Serializable> extends
		SimpleDaoImpl<T, PK> implements IBaseDAO<T, PK> {

	public Query setPageParameter(final Query query, final int fisrt,
			final int limit) {
		// hibernate的firstResult的序号从0开始
		query.setFirstResult(fisrt);
		query.setMaxResults(limit);
		return query;
	}

	public long countSqlResult(final String sql, final Object... values) {

		String countSql = "select count(1) from (" + sql + ")";

		Long count = 0L;
		try {
			count = ((Number) createSQLQuery(countSql, values).uniqueResult())
					.longValue();
		} catch (Exception e) {
			throw new RuntimeException("sql can't be auto count, hql is:"
					+ countSql, e);
		}
		return count;
	}

	public long countSqlResult(final String sql,
			final Map<String, Object> values) {
		String countSql = "select count(1) from (" + sql + ")";

		Long count = 0L;
		try {
			count = ((Number) createSQLQuery(countSql, values).uniqueResult())
					.longValue();
		} catch (Exception e) {
			throw new RuntimeException("sql can't be auto count, hql is:"
					+ countSql, e);
		}
		return count;
	}

	public Page findPageBySQL(BaseVo vo, String sql, Map<String, Object> values) {
		Page page = new Page();
		// count查询
		long totalCount = countSqlResult(sql, values);
		page.setTotalCount(totalCount);
		page.setPageSize(vo.getLimit());
		page.setPageIndex(vo.getStart() / vo.getLimit() + 1);

		if (totalCount > 0) {
			// 按分页查询结果集
			SQLQuery query = createSQLQuery(sql, values);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			setPageParameter(query, vo.getStart(), vo.getLimit());
			List result = query.list();
			page.setData(result);
		}

		return page;
	}

	public Page findPageBySQL(BaseVo vo, String sql, Object... values) {
		Page page = new Page();
		// count查询
		long totalCount = countSqlResult(sql, values);
		page.setTotalCount(totalCount);
		page.setPageSize(vo.getLimit());
		page.setPageIndex(vo.getStart() / vo.getLimit() + 1);

		if (totalCount > 0) {
			// 按分页查询结果集
			SQLQuery query = createSQLQuery(sql, values);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			setPageParameter(query, vo.getStart(), vo.getLimit());
			List result = query.list();
			page.setData(result);
		}

		return page;
	}

	public Map<String, Object> findUniqueBySQL(String sql,
			Map<String, Object> values) {
		SQLQuery query = createSQLQuery(sql, values);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) query.uniqueResult();
	}

	public Map<String, Object> findUniqueBySQL(String sql, Object... values) {
		SQLQuery query = createSQLQuery(sql, values);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) query.uniqueResult();
	}
}
