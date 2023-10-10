import cv2
import numpy as np
from matplotlib import pyplot as plt


def read_image(image_name):
    return cv2.imread(image_name, cv2.IMREAD_GRAYSCALE)


def write_image(image_name, image_data):
    cv2.imwrite(image_name, image_data)


BLOCK_SIZE = 8


class DctCompressor:
    def __init__(self, block_size=BLOCK_SIZE):
        self.block_size = block_size

    def compress(self, input_image_name, output_image_name):
        input_image = read_image(input_image_name)
        result_image = self.__compress(input_image)
        write_image(output_image_name, result_image)

    def __compress(self, input_image):
        height, width = input_image.shape
        result_image = np.ones_like(input_image)

        for i in range(0, height, self.block_size):
            for j in range(0, width, self.block_size):
                current_block = input_image[i:i + self.block_size, j:j + self.block_size]
                dct_block = self.__dct(current_block)
                result_image[i:i + self.block_size, j:j + self.block_size] = dct_block

        return result_image

    def __dct(self, block):
        result = np.zeros_like(block, dtype=float)

        for i in range(self.block_size):
            for j in range(self.block_size):
                result[i, j] = self.__dct_elem(block, i, j)

        return result

    def __dct_elem(self, block, i, j):
        sum = 0

        for x in range(0, self.block_size):
            for y in range(0, self.block_size):
                sum += block[x, y] * np.cos((2*x + 1) * i * np.pi / (2*self.block_size)) * np.cos((2*y + 1) * j * np.pi / (2*self.block_size))

        c_i = self.__get_c(i)
        c_j = self.__get_c(j)

        return sum * c_i * c_j / np.sqrt(2 * self.block_size)

    def __get_c(self, index):
        return 1 / np.sqrt(2) if index == 0 else 1


class WaveletCompressor:
    def __init__(self, block_size=BLOCK_SIZE):
        self.block_size = block_size

    def compress(self, input_image_name, output_image_name):
        input_image = read_image(input_image_name)
        result_image = self.__compress(input_image)
        write_image(output_image_name, result_image)

    def __compress(self, input_image):
        height, width = input_image.shape
        result_image = np.ones_like(input_image)

        for i in range(0, height, self.block_size):
            for j in range(0, width, self.block_size):
                current_block = input_image[i:i + self.block_size, j:j + self.block_size]
                dct_block = self.__wavelet(current_block)
                result_image[i:i + self.block_size, j:j + self.block_size] = dct_block

        return result_image

    def __wavelet(self, block):
        length = len(block)
        h = length // 2

        LL = block[:h, :h]
        LH = block[:h, h:]
        HL = block[h:, :h]
        HH = block[h:, h:]

        LL_dwt = np.mean(LL)
        LH_dwt = LH - np.mean(LH)
        HL_dwt = HL - np.mean(HL)
        HH_dwt = HH - np.mean(HH)

        common_shape = LH.shape

        LL_dwt = np.full(common_shape, LL_dwt)
        LH_dwt = LH_dwt

        result = np.vstack((
            np.hstack((LL_dwt, LH_dwt)),
            np.hstack((HL_dwt, HH_dwt))
        ))

        return result


def psnr(first_image, second_image, max_pixel=255):
    mse = np.mean((first_image - second_image) ** 2)

    return 10 * np.log10((max_pixel ** 2) / mse)


def psnr_by_names(first_image_name, second_image_name, max_pixel=255):
    first_image = read_image(first_image_name)
    second_image = read_image(second_image_name)

    return psnr(first_image, second_image, max_pixel)


def demo(input_image_name, dct_image_name, wavelet_image_name):
    input_image = read_image(input_image_name)
    dct_image = read_image(dct_image_name)
    wavelet_image = read_image(wavelet_image_name)

    fig, axes = plt.subplots(1, 3, figsize=(12, 6))

    axes[0].imshow(input_image, cmap='gray')
    axes[0].set_title('Input image')
    axes[0].axis('off')

    axes[1].imshow(dct_image, cmap='gray')
    axes[1].set_title(f'DCT algorithm(PSNR={psnr(input_image, dct_image):.3f} dB)')
    axes[1].axis('off')

    axes[2].imshow(wavelet_image, cmap='gray')
    axes[2].set_title(f'Wavelet algorithm(PSNR={psnr(input_image, wavelet_image):.3f} dB)')
    axes[2].axis('off')

    plt.show()


def main():
    input_image_name = 'input_image.png'
    output_image_name_dct = 'dct_compressed.png'
    dct_compressor = DctCompressor()

    output_image_name_wavelet = "wavelet_compressed.png"
    wavelet_compressor = WaveletCompressor()

    print('Started compressing by DCT')
    dct_compressor.compress(input_image_name, output_image_name_dct)
    print('Finished compressing by DCT')

    print('Started compressing by wavelet')
    wavelet_compressor.compress(input_image_name, output_image_name_wavelet)
    print('Finished compressing by wavelet')

    demo(input_image_name, output_image_name_dct, output_image_name_wavelet)


if __name__ == '__main__':
    main()
