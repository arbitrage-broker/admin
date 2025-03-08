package com.arbitragebroker.admin.repository;

import com.arbitragebroker.admin.entity.WalletEntity;
import com.arbitragebroker.admin.enums.EntityStatusType;
import com.arbitragebroker.admin.enums.TransactionType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends BaseRepository<WalletEntity, Long> {

//	@Query("""
//       	SELECT coalesce(
//			SUM(CASE WHEN w.transactionType = enums.com.arbitragebroker.admin.TransactionType.DEPOSIT OR w.transactionType = enums.com.arbitragebroker.admin.TransactionType.REWARD OR w.transactionType = enums.com.arbitragebroker.admin.TransactionType.BONUS THEN w.amount ELSE 0 END) -
//			SUM(CASE WHEN w.transactionType = enums.com.arbitragebroker.admin.TransactionType.WITHDRAWAL OR w.transactionType = enums.com.arbitragebroker.admin.TransactionType.WITHDRAWAL_PROFIT THEN w.amount ELSE 0 END),0)
//			FROM WalletEntity w WHERE w.user.id = :userId AND w.status=enums.com.arbitragebroker.admin.EntityStatusType.Active""")
//	BigDecimal totalBalanceByUserId(UUID userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
	@Query("select w from WalletEntity w where w.user.id = :userId and w.status = com.arbitragebroker.admin.enums.EntityStatusType.Active")
	List<WalletEntity> findAllActiveByUserId(UUID userId);

	@Transactional
	default BigDecimal calculateUserBalance(UUID userId) {
		List<WalletEntity> list = findAllActiveByUserId(userId);

		BigDecimal deposits = list.parallelStream()
				.filter(w -> EnumSet.of(TransactionType.DEPOSIT, TransactionType.REWARD, TransactionType.BONUS)
						.contains(w.getTransactionType()))
				.map(WalletEntity::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal withdrawals = list.parallelStream()
				.filter(w -> EnumSet.of(TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_PROFIT)
						.contains(w.getTransactionType()))
				.map(WalletEntity::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return deposits.subtract(withdrawals);
	}

	@Query("""
			SELECT coalesce(
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.DEPOSIT THEN w.amount ELSE 0 END) - 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w WHERE w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalBalanceByUserId(UUID userId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE w.transactionType = com.arbitragebroker.admin.enums.TransactionType.DEPOSIT
			AND w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalDepositByUserId(UUID userId);

	@Query("""
			SELECT coalesce(
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.DEPOSIT THEN w.amount ELSE 0 END) - 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w join w.user u WHERE u.parent.id = :parentId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalBalanceOfSubUsers(UUID parentId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL_PROFIT) 
			AND w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalWithdrawalOrProfitByUserId(UUID userId);
	@Query("""
			SELECT coalesce(SUM(w.amount),0)
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL)
			AND w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalWithdrawalByUserId(UUID userId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0)
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL_PROFIT)
			AND w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalWithdrawalProfitByUserId(UUID userId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.admin.enums.TransactionType.BONUS)
			AND w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalBonusByUserId(UUID userId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.admin.enums.TransactionType.REWARD) 
			AND w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalRewardByUserId(UUID userId);

	@Query("""			
			SELECT coalesce( 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.REWARD OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.REWARD_REFERRAL OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.BONUS THEN w.amount ELSE 0 END) -
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL_PROFIT THEN w.amount ELSE 0 END), 0)
			FROM WalletEntity w WHERE w.user.id = :userId AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalProfitByUserId(UUID userId);

	@Query("""
			SELECT coalesce(
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.DEPOSIT THEN w.amount ELSE 0 END) - 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL_PROFIT THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w join w.user u join u.roles r WHERE r.id in (1,2) AND (:role is null or w.role=:role) AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalBalance(String role);

	@Query("""
			SELECT coalesce(SUM(w.amount),0)
			FROM WalletEntity w join w.user u join u.roles r
			WHERE r.id = 2 AND (:role is null or w.role=:role) AND w.transactionType = com.arbitragebroker.admin.enums.TransactionType.DEPOSIT
			AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalDeposit(String role);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w join w.user u join u.roles r 
			WHERE r.id in (1,2) AND (:role is null or w.role=:role) AND (w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL_PROFIT)
			AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalWithdrawal(String role);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w join w.user u join u.roles r 
			WHERE r.id = 2 AND w.role=:role AND w.transactionType = com.arbitragebroker.admin.enums.TransactionType.BONUS 
			AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalBonus(String role);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w join w.user u join u.roles r 
			WHERE r.id = 2 AND (:role is null or w.role=:role) AND w.transactionType = com.arbitragebroker.admin.enums.TransactionType.REWARD 
			AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalReward(String role);

	@Query("""
			SELECT coalesce( 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.REWARD OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.REWARD_REFERRAL OR w.transactionType = com.arbitragebroker.admin.enums.TransactionType.BONUS THEN w.amount ELSE 0 END) - 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.admin.enums.TransactionType.WITHDRAWAL_PROFIT THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w join w.user u join u.roles r WHERE r.id = 2 AND (:role is null or w.role=:role) AND w.status=com.arbitragebroker.admin.enums.EntityStatusType.Active""")
	BigDecimal totalProfit(String role);

	long countByUserIdAndTransactionTypeAndStatus(UUID userId, TransactionType transactionType, EntityStatusType status);//because it will call after create and activation
	List<WalletEntity> findAllByStatusAndTransactionHashIsNotNullAndTransactionType(EntityStatusType status, TransactionType transactionType);

    long countAllByUserIdAndTransactionTypeAndStatus(UUID userId, TransactionType transactionType, EntityStatusType status);

//	@Query(value = "SELECT currency, SUM(totalAmount) AS totalAmount" +
//			" FROM (" +
//			" SELECT w.currency AS currency, SUM(w.amount) AS totalAmount" +
//			" FROM tbl_wallet w" +
//			" WHERE w.transaction_type = 'BONUS' AND w.user_id = :userId AND w.status = 'Active'" +
//			" GROUP BY w.currency" +
//			" UNION ALL" +
//			" SELECT a.currency AS currency, SUM(a.reward) AS totalAmount" +
//			" FROM tbl_arbitrage a" +
//			" WHERE a.user_id = :userId" +
//			" GROUP BY a.currency" +
//			" ) AS combined" +
//			" GROUP BY currency;", nativeQuery = true)
//	List<Object[]> totalProfit(UUID userId);


}
