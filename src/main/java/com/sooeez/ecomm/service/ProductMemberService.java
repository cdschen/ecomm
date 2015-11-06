package com.sooeez.ecomm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sooeez.ecomm.domain.ProductMember;
import com.sooeez.ecomm.repository.ProductMemberRepository;

@Service
public class ProductMemberService {

	/*
	 * Repository
	 */

	@Autowired
	private ProductMemberRepository memberRepository;

	/*
	 * ProductMember
	 */

	@Transactional
	public ProductMember saveMember(ProductMember member) {
		return memberRepository.save(member);
	}

	@Transactional
	public void deleteMember(Long id) {
		memberRepository.delete(id);
	}

	public ProductMember getMember(Long id) {
		return memberRepository.findOne(id);
	}

	public List<ProductMember> getMembers() {
		return memberRepository.findAll();
	}

	public Page<ProductMember> getPagedMembers(Pageable pageable) {
		return memberRepository.findAll(pageable);
	}
}
