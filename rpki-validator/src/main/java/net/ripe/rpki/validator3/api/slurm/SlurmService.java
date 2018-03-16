/**
 * The BSD License
 *
 * Copyright (c) 2010-2018 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.rpki.validator3.api.slurm;

import net.ripe.rpki.validator3.api.ignorefilters.AddIgnoreFilter;
import net.ripe.rpki.validator3.api.ignorefilters.IgnoreFilterService;
import net.ripe.rpki.validator3.api.roaprefixassertions.AddRoaPrefixAssertion;
import net.ripe.rpki.validator3.api.roaprefixassertions.RoaPrefixAssertionsService;
import net.ripe.rpki.validator3.api.slurm.dtos.Slurm;
import net.ripe.rpki.validator3.api.slurm.dtos.SlurmLocallyAddedAssertions;
import net.ripe.rpki.validator3.api.slurm.dtos.SlurmOutputFilters;
import net.ripe.rpki.validator3.api.slurm.dtos.SlurmPrefixAssertion;
import net.ripe.rpki.validator3.api.slurm.dtos.SlurmPrefixFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

/*
    TODO Add BGPSec-related functionality
 */
@Service
public class SlurmService {

    @Autowired
    private RoaPrefixAssertionsService roaPrefixAssertionsService;

    @Autowired
    private IgnoreFilterService ignoreFilterService;

    @Transactional(Transactional.TxType.REQUIRED)
    public void process(Slurm slurm) {
        if (slurm.getLocallyAddedAssertions() != null && slurm.getLocallyAddedAssertions().getPrefixAssertions() != null) {
            slurm.getLocallyAddedAssertions().getPrefixAssertions().forEach(prefixAsertion -> {
                final AddRoaPrefixAssertion add = AddRoaPrefixAssertion.builder()
                        .asn(prefixAsertion.getAsn() == null ? null : prefixAsertion.getAsn().toString())
                        .prefix(prefixAsertion.getPrefix())
                        .maximumLength(prefixAsertion.getMaxPrefixLength())
                        .comment(prefixAsertion.getComment())
                        .build();
                roaPrefixAssertionsService.execute(add);
            });
        }

        if (slurm.getValidationOutputFilters() != null && slurm.getValidationOutputFilters().getPrefixFilters() != null) {
            slurm.getValidationOutputFilters().getPrefixFilters().forEach(prefixFilter -> {
                final AddIgnoreFilter addIgnoreFilter = AddIgnoreFilter.builder()
                        .asn(prefixFilter.getAsn() == null ? null : prefixFilter.getAsn().toString())
                        .prefix(prefixFilter.getPrefix())
                        .comment(prefixFilter.getComment())
                        .build();
                ignoreFilterService.execute(addIgnoreFilter);
            });
        }
    }

    public Slurm get() {
        final Slurm slurm = new Slurm();

        SlurmLocallyAddedAssertions slurmLocallyAddedAssertions = new SlurmLocallyAddedAssertions();
        slurmLocallyAddedAssertions.setPrefixAssertions(roaPrefixAssertionsService.all().map(a -> {
            SlurmPrefixAssertion prefixAssertion = new SlurmPrefixAssertion();
            prefixAssertion.setAsn(a.getAsn());
            prefixAssertion.setPrefix(a.getPrefix());
            prefixAssertion.setMaxPrefixLength(a.getMaximumLength());
            prefixAssertion.setComment(a.getComment());
            return prefixAssertion;
        }).collect(Collectors.toList()));
        slurm.setLocallyAddedAssertions(slurmLocallyAddedAssertions);

        SlurmOutputFilters filters = new SlurmOutputFilters();
        filters.setPrefixFilters(ignoreFilterService.all().stream().map(f -> {
            SlurmPrefixFilter prefixFilter = new SlurmPrefixFilter();
            prefixFilter.setAsn(f.getAsn());
            prefixFilter.setPrefix(f.getPrefix());
            prefixFilter.setComment(f.getComment());
            return prefixFilter;
        }).collect(Collectors.toList()));
        slurm.setValidationOutputFilters(filters);

        return slurm;
    }
}
