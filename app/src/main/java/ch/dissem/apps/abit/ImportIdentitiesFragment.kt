/*
 * Copyright 2016 Christian Basler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.dissem.apps.abit

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator

import ch.dissem.apps.abit.adapter.AddressSelectorAdapter
import ch.dissem.apps.abit.service.Singleton
import ch.dissem.bitmessage.wif.WifImporter

/**
 * @author Christian Basler
 */
class ImportIdentitiesFragment : Fragment() {
    private lateinit var adapter: AddressSelectorAdapter
    private lateinit var importer: WifImporter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View =
            inflater.inflate(R.layout.fragment_import_select_identities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wifData = arguments.getString(WIF_DATA)
        val bmc = Singleton.getBitmessageContext(activity)

        importer = WifImporter(bmc, wifData)
        adapter = AddressSelectorAdapter(importer.getIdentities())
        val layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false)
        val recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(SimpleListDividerDecorator(
                ContextCompat.getDrawable(activity, R.drawable.list_divider_h), true))

        view.findViewById(R.id.finish).setOnClickListener {
            importer.importAll(adapter.selected)
            val mainActivity = MainActivity.getInstance()
            if (mainActivity != null) {
                for (selected in adapter.selected) {
                    mainActivity.addIdentityEntry(selected)
                }
            }
            activity.finish()
        }
    }

    companion object {
        val WIF_DATA = "wif_data"
    }
}
