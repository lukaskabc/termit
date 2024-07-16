/*
 * TermIt
 * Copyright (C) 2023 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.termit.service.changetracking;

import cz.cvut.kbss.termit.model.Asset;
import cz.cvut.kbss.termit.model.changetracking.UpdateChangeRecord;

import java.util.Collection;

/**
 * Calculates changes made to an asset when compared to its original from the repository.
 */
public interface ChangeCalculator {

    /**
     * Calculates the set of changes made to the specified asset.
     * <p>
     * Note that this method assumes the asset already exists, so creation change records are not generated by it. Also,
     * the implementations need not generate provenance data (timestamp, authorship) for the change records.
     *
     * @param changed  The updated asset
     * @param original The original asset against which changes are calculated
     * @return A collection of changes made to the asset
     */
    Collection<UpdateChangeRecord> calculateChanges(Asset<?> changed, Asset<?> original);
}
