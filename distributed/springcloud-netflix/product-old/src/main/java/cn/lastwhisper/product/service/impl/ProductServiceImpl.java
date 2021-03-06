package cn.lastwhisper.product.service.impl;

import cn.lastwhisper.product.domain.ProductInfo;
import cn.lastwhisper.product.dto.CartDTO;
import cn.lastwhisper.product.enums.ProductStatusEnum;
import cn.lastwhisper.product.enums.ResultEnum;
import cn.lastwhisper.product.exception.ProductExecption;
import cn.lastwhisper.product.repository.ProductInfoRepository;
import cn.lastwhisper.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 商品业务
 * @author lastwhisper
 * @date 2019/10/26
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public List<ProductInfo> findUpAll() {
        // 不要直接使用硬编码
        return productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public List<ProductInfo> findList(List<String> productIdList) {
        return productInfoRepository.findByProductIdIn(productIdList);
    }

    /**
     * 扣库存
     *  1.先检查商品是否存在
     *  2.再检查库存是否充足
     *  3.有一项不通过抛异常回滚事务
     */
    @Override
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO : cartDTOList) {
            Optional<ProductInfo> optionalProductInfo = productInfoRepository.findById(cartDTO.getProductId());
            // 1.检查商品是否存在
            if (!optionalProductInfo.isPresent()) {
                //log.error("[扣库存]商品不存在 cartDTO={}", cartDTO);
                throw new ProductExecption(ResultEnum.PRODUCT_NOT_EXIST);
            }
            // 2.检查库存是否充足
            ProductInfo productInfo = optionalProductInfo.get();
            Integer surplusStock = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (surplusStock < 0) {
                //log.error("[扣库存]商品剩余库存不足 cartDTO={}", cartDTO);
                throw new ProductExecption(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            // 3.保存库存
            productInfo.setProductStock(surplusStock);
            productInfoRepository.save(productInfo);

        }
    }

}
