package com.rmoraes.minhasfinancas.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rmoraes.minhasfinancas.exception.AutenticacaoException;
import com.rmoraes.minhasfinancas.exception.RegraNegocioException;
import com.rmoraes.minhasfinancas.model.entity.Usuario;
import com.rmoraes.minhasfinancas.model.repository.UsuarioRepository;
import com.rmoraes.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
	
	private final UsuarioRepository repository;

	@Override
	@Transactional(readOnly = true)
	public Usuario autenticar(final String email, final String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		validarUsuario(usuario, senha);
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvar(final Usuario usuario) {
		validarEmail(usuario.getEmail());
		usuario.setDataCadastro(LocalDate.now());
		return repository.save(usuario);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Usuario obterPorId(final Long id) {
		final Optional<Usuario> usuario = repository.findById(id);
		if(!usuario.isPresent()) {
			throw new RegraNegocioException("Usuário não encontrado para o id informado.");
		}
		return usuario.get();
	}

	@Override
	public void validarEmail(final String email) {
		if(repository.existsByEmail(email)) {
			throw new RegraNegocioException("Já existe um usuário cadasrado com este email.");
		}
	}
	
	private void validarUsuario(Optional<Usuario> usuario, final String senha) {
		validarEmail(usuario);
		validarSenha(usuario, senha);
	}

	private void validarEmail(Optional<Usuario> usuario) {
		if(!usuario.isPresent()) {
			throw new AutenticacaoException("Usuário não encontrado para o email informado.");
		}
	}

	private void validarSenha(Optional<Usuario> usuario, final String senha) {
		if(!usuario.get().getSenha().equals(senha)) {
			throw new AutenticacaoException("Senha inválida.");
		}
	}

}
