/*******************************************************************************
 * Copyright (c) 2019, 2023 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.liveprocess;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.lsp4e.LanguageServers;
import org.eclipse.lsp4e.LanguageServers.LanguageServerProjectExecutor;
import org.eclipse.lsp4e.LanguageServersRegistry;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.ExecuteCommandParams;

import com.google.common.collect.ImmutableList;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("restriction")
public final class DefaultLiveProcessCommandExecutor implements LiveProcessCommandsExecutor {

	private static final String CMD_LIST_PROCESSES = "sts/livedata/listProcesses";

	@SuppressWarnings("unchecked")
	@Override
	public Flux<CommandInfo> listCommands() {
		List<CompletableFuture<List<CommandInfo>>> futures = getLanguageServers().computeAll(ls -> ls.getWorkspaceService().executeCommand(new ExecuteCommandParams(
				CMD_LIST_PROCESSES,
				ImmutableList.of()
		)).thenApply(o -> {
			if (o instanceof List) {
				List<?> list = (List<?>) o;
				return list.stream().map(_cmdInfo -> {
					Map<String,String> map = (Map<String, String>) _cmdInfo;
					return new CommandInfo(map.get("action"), map);
				}).collect(Collectors.toList());
			}
			return Collections.<CommandInfo>emptyList();
		}));
		Flux<CommandInfo> f = Flux.fromIterable(futures).flatMap(fo -> Mono.fromFuture(fo).flatMapIterable(l -> l));
		return f;
	}

	@Override
	public Mono<Void> executeCommand(CommandInfo cmd) {
		return Mono.fromFuture(getLanguageServers().collectAll(ls -> ls.getWorkspaceService().executeCommand(new ExecuteCommandParams(
				cmd.command,
				ImmutableList.of(cmd.info)
		))).thenAccept(null));
	}

	private LanguageServerProjectExecutor getLanguageServers() {
		return LanguageServers.forProject(null).withFilter(cap -> {
			ExecuteCommandOptions commandCap = cap.getExecuteCommandProvider();
			if (commandCap!=null) {
				List<String> supportedCommands = commandCap.getCommands();
				return supportedCommands!=null && supportedCommands.contains(CMD_LIST_PROCESSES);
			}
			return false;
		}).withPreferredServer(LanguageServersRegistry.getInstance()
					.getDefinition("org.eclipse.languageserver.languages.springboot"));
	}
}