package com.noori.komoran.util;

import com.noori.komoran.core.Komoran;
import com.noori.komoran.model.KomoranResult;

import java.util.concurrent.Callable;

/**
 * Created by shin285 on 2017. 4. 4..
 */
public class KomoranCallable implements Callable<KomoranResult>{

	private final Komoran komoran;
	private final String input;

	public KomoranCallable(Komoran komoran, String input){
		this.komoran = komoran;
		this.input = input;
	}

	@Override
	public KomoranResult call() throws Exception {
		return this.komoran.analyze(this.input);
	}
}
