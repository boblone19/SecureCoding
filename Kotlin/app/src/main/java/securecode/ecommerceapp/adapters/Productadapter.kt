package securecodewarrior.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.product_list_item.view.*
import securecodewarrior.ecommerceapp.R
import securecodewarrior.ecommerceapp.model.Product

class ProductAdapter(val items: List<Product>, val context: Context)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.product_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items.get(position)
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = "${product.price} $"
    }


    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName = view.productName
        val tvProductPrice = view.productPrice
    }

}


