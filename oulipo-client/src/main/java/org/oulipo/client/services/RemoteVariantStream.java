package org.oulipo.client.services;

import java.io.IOException;
import java.util.List;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.InvariantSpan;
import org.oulipo.streams.InvariantSpans;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;

public class RemoteVariantStream implements VariantStream {

	private TumblerService service;

	public RemoteVariantStream(TumblerService service) {
		this.service = service;
	}

	@Override
	public void copy(long characterPosition, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		// service.copy(document, position, spans, callback);

	}

	@Override
	public void delete(VariantSpan variantSpan) throws MalformedSpanException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public InvariantSpans getInvariantSpans() throws MalformedSpanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InvariantSpans getInvariantSpans(VariantSpan variantSpan) throws MalformedSpanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InvariantSpan index(long characterPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(long characterPosition, InvariantSpan invariantSpan) throws MalformedSpanException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void swap(VariantSpan v1, VariantSpan v2) throws MalformedSpanException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<VariantSpan> getVariantSpans(InvariantSpan invariantSpan) throws MalformedSpanException {
		// TODO Auto-generated method stub
		return null;
	}

}
