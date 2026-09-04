package com.mantenimiento.soportetecnicoapp.vistas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mantenimiento.soportetecnicoapp.data.entity.ClienteEntity
import com.mantenimiento.soportetecnicoapp.databinding.ItemClienteBinding

class ClienteAdapter(
    private var clientes: List<ClienteEntity>,
    private val onItemClick: (ClienteEntity) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(val binding: ItemClienteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.binding.tvNombreItem.text = cliente.Nombre_cl
        holder.binding.tvTelefonoItem.text = "Teléfono: ${cliente.Telefono}"
        holder.binding.tvCorreoItem.text = "Correo: ${cliente.Correo}"

        holder.itemView.setOnClickListener {
            onItemClick(cliente)
        }
    }

    override fun getItemCount(): Int = clientes.size

    fun updateData(newClientes: List<ClienteEntity>) {
        this.clientes = newClientes
        notifyDataSetChanged()
    }
}
