package com.cod.dao;

import com.cod.dto.codi.selectcodi.QSelectCodiOutput;
import com.cod.dto.codi.selectcodi.SelectCodiInput;
import com.cod.dto.codi.selectcodi.SelectCodiOutput;
import com.cod.entity.QCodi;
import com.cod.entity.QCodiLiked;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class CodiRepositoryImpl implements CodiRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    QCodi qCodi = QCodi.codi;
    QCodiLiked qCodiLiked = QCodiLiked.codiLiked;

    @Override
    public Page<SelectCodiOutput> findByDynamicQuery(SelectCodiInput selectCodiInput, Pageable pageable) {
        QueryResults<SelectCodiOutput> queryResult = queryFactory
                .select(new QSelectCodiOutput(
                        qCodi.user.id,
                        qCodi.name,
                        qCodi.tag,
                        qCodi.description,
                        qCodi.thumbnail,
                        qCodi.coordinate,
                        qCodi.created_at,
                        qCodi.updated_at,
                        //liked
                        JPAExpressions.select(qCodiLiked.count().castToNum(Integer.class)).from(qCodiLiked)
                                .where(qCodiLiked.codi.id.eq(qCodi.id))
                ))
                .from(qCodi)
                .where(eqTag(selectCodiInput.getTag()), eqDescription(selectCodiInput.getDescription()), eqName(selectCodiInput.getName()), eqUserId(selectCodiInput.getUserId()))
                .orderBy(qCodi.created_at.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetchResults();
        long totalCount = queryResult.getTotal();
        List<SelectCodiOutput> content = queryResult.getResults();

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression eqName(String word) {
        if (StringUtils.isEmpty(word)) {
            return null;
        }
        return qCodi.name.contains(word);
    }

    private BooleanExpression eqDescription(String word) {
        if (StringUtils.isEmpty(word)) {
            return null;
        }
        return qCodi.description.contains(word);
    }

    private BooleanExpression eqTag(String tag) {
        if (StringUtils.isEmpty(tag)) {
            return null;
        }
        return qCodi.name.contains(tag);
    }

    private BooleanExpression eqUserId(Integer userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return qCodi.user.id.eq(userId);
    }

}

