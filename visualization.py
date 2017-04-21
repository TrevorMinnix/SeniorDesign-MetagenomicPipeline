import matplotlib.pyplot as plot
import sys

#Create a table of statistics for each assembler
def Create_table(cell_text, output_dir):
    #These four values manage the size and format of the letters
    scaling = 2.0
    ext_scaling = 10.0
    lHeight_coeff = 0.10
    lWidth_coeff = 0.04
    
    row_height = lHeight_coeff * scaling
    nrows = len(cell_text)
    
    rows = ['Assembly Length (bp)','# of Contigs','# of Clean Windows'
            ,'# of Large Contigs', 'GC Comp.', '# Windows', 'N50', 'N75', 'Avg. # of N per 100 kbp'
            ,'Aligned Genome','Duplication Rate','# of Gaps','Largest Alignment Length'
            ,'# of Fully Unaligned Reads','# of Mapped Reads']
    
    col_widths = [len(max(rows, key = len)), len(max([str(i[0]) for i in cell_text], key = len))]

    external_text_height = float(lHeight_coeff * ext_scaling)
    total_height = nrows * row_height + 2 * external_text_height
    
    total_width = lWidth_coeff * scaling * sum(col_widths) * 1.5
    
    plot.figure(figsize=(total_width, total_height))
    plot.axis('off')
    
    plot.table( cellText=cell_text,
                rowLabels=rows,
                colWidths=[float(column_width) / sum(col_widths) for column_width in col_widths[1:]],
                rowLoc = 'left', loc='center right')
    
    plot.savefig(output_dir +'table.jpg')
    
    plot.cla()
    plot.close()

#Opens and reads the file containing statistics
def openStatFile(inputFile):
    file = open(inputFile, 'r') 
    statistics = []
    statistics.append([int(file.readline())])
    statistics.append([int(file.readline())])
    
    winSize, cleanWin = file.readline().split(' ')
    statistics.append([int(cleanWin)])

    statistics.append([int(file.readline())])
    
    file.readline()

    contigSizes = file.readline().split(' ')
    contigSizes.remove('\n')
    contigSizes = map(int, contigSizes)
    
    statistics.append([float(file.readline())])
    
    winSize, gcContent = file.readline().split(' ')
    statistics.append([int(gcContent)])
    
    gcCont = file.readline().split(' ')
    gcCont.remove('\n')
    gcCont = map(float, gcCont)
    
    nxArray = file.readline().split(' ')
    nxArray.remove('\n')
    nxArray = map(int, nxArray)
    
    statistics.append([nxArray[49]])
    statistics.append([nxArray[74]])
    bpRange, numN = file.readline().split(' ')
    statistics.append([float(numN)])
    
    for i in range(6):
        if(i>=2):
            statistics.append([int(file.readline())])
        else:
            statistics.append([float(file.readline())])

    file.close()
    
    return statistics, contigSizes, gcContent, gcCont, nxArray    

#Plots the Nx-value graph for the assembly
def Nx_plot(nxArray, output_dir):
    plot.plot(nxArray, linewidth = 2.0)
    plot.ylabel('Contig Length (bp)')
    plot.grid(b = True)
    plot.title('Nx-Plot')
    plot.savefig(output_dir + 'Nx_Plot.jpg')
    plot.cla()
    plot.close()

#Plots the cumulative length
def CumulativePlot(contigSizes, output_dir):
    prevContig = 0
    #contigSizes = contigSizes[::-1]

    cumulContig = [0] * len(contigSizes)
    for i in xrange(len(contigSizes)):
        cumulContig[i] = prevContig + contigSizes[i]
        prevContig += contigSizes[i]
    
    plot.plot(cumulContig, linewidth = 2.0)
    plot.ylabel('Contig Length (bp)')
    plot.xlabel('Contig Index')
    plot.grid(b = True)
    plot.title('Cumulative Length')
    plot.savefig(output_dir+'CumulPlot.jpg')
    plot.cla()
    plot.close()

#Plots a frequency graph of GC content
def GcPlot(gcCont, numWindows, output_dir):
    gcHist = [0] * 100
    for gc in xrange(int(numWindows)):
        gcHist[int(100*gcCont[gc]) - 1] += 1
    plot.plot(gcHist, linewidth = 0.5)
    plot.ylabel('# Windows')
    plot.xlabel('GC (%)')
    plot.grid(b = True)
    plot.title('GC content')
    plot.savefig(output_dir+'GCPlot.jpg')
    plot.cla()
    plot.close()
 
#The main function, takes two command line arguments
#First is the input file of statistics
#Second arg is the output directory
def Visualize():
    inputFile, output_dir = sys.argv[1], sys.argv[2]
    statistics, contigSizes, numWindows, gcCont, nxArray = openStatFile(inputFile)
    Create_table(statistics, output_dir)
    Nx_plot(nxArray, output_dir)
    CumulativePlot(contigSizes, output_dir)
    GcPlot(gcCont, numWindows, output_dir)
    
Visualize()