import matplotlib.pyplot as plot
import matplotlib.patches as patch
import sys

#Create a table of statistics for each assembler
def Create_table(cell_text, output_dir, comb_dir, labels):
    #These four values manage the size and format of the letters
    scaling = 2.0
    ext_scaling = 10.0
    lHeight_coeff = 0.10
    lWidth_coeff = 0.04
    nrows = len(cell_text[0])
    
    row_height = lHeight_coeff * scaling
    
    rows = ['Assembly Length (bp)','# of Contigs','# of Clean Windows'
            ,'# of Large Contigs', 'GC Comp.', '# of Windows', 'N50', 'N75', 'Avg. # of N per 100 kbp'
            ,'Aligned Genome','Duplication Rate','# of Gaps','Largest Alignment Length'
            ,'# of Fully Unaligned Reads','# of Mapped Reads']
    
    external_text_height = float(lHeight_coeff * ext_scaling)
    total_height = nrows * row_height + 2 * external_text_height
    
    for i in xrange(len(cell_text)):
        
        col_widths = [len(max(rows, key = len)), len(max([str(stat[0]) for stat in cell_text[i]], key = len))]
        
        total_width = lWidth_coeff * scaling * sum(col_widths) * 1.5
        
        plot.figure(figsize=(total_width, total_height))
        plot.axis('off')
        
        plot.table( cellText=cell_text[i],
                    rowLabels=rows,
                    colWidths=[float(column_width) / sum(col_widths) for column_width in col_widths[1:]],
                    rowLoc = 'left', loc='center right')
        
        plot.savefig(output_dir[i] +'table.jpg')
        
        plot.cla()
        plot.close()
    
    row_data = []
    for row in range(len(cell_text[0])):
        curRow = []
        for col in range(len(cell_text)):
            curRow.append(str(cell_text[col][row][0]))
        
        row_data.append(curRow)
    
    col_widths = [len(max(rows, key = len))]
    for col in range(len(cell_text)):
        width = len(max([str(stat[0]) for stat in cell_text[col]], key = len))
        col_widths.append(width)
        
    total_width = lWidth_coeff * scaling * sum(col_widths) * 1.5
        
    plot.figure(figsize=(total_width, total_height))
    plot.axis('off')
    plot.table( cellText= row_data,
                    rowLabels=rows,
                    colLabels = labels,
                    colWidths=[float(column_width) / sum(col_widths) for column_width in col_widths[1:]],
                    rowLoc = 'left', colLoc = 'right', loc='center right')
        
    plot.savefig(comb_dir +'table.jpg')
        
    plot.cla()
    plot.close()

#Opens and reads the file containing statistics
def openStatFile(inputFiles):
    allStats = []
    allContigs = []
    allWindows = []
    allGCC = []
    allNX = []
    
    for input in inputFiles:
        file = open(input, 'r') 
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
        
        allStats.append(statistics)
        allContigs.append(contigSizes)
        allWindows.append(gcContent)
        allGCC.append(gcCont)
        allNX.append(nxArray)
    
    return allStats, allContigs, allWindows, allGCC, allNX    

#Plots the Nx-value graph for the assembly
def Nx_plot(nxArray, output_dir, comb_dir, labels):
    handler = []
    for i in xrange(len(nxArray)):
        plot.figure(1)
        plot.plot(nxArray[i], linewidth = 2.0)
        plot.ylabel('Contig Length (bp)')
        plot.grid(b = True)
        plot.title('Nx-Plot')
        plot.savefig(output_dir[i] + 'Nx_Plot.jpg')
        plot.cla()
        plot.close()
        
        plot.figure(2)
        fig = plot.plot(nxArray[i], linewidth = 2.0)
        handler.append(patch.Patch(color=fig[-1].get_color(), label=labels[i]))
    
    plot.ylabel('Contig Length (bp)')
    plot.grid(b = True)
    plot.title('Nx-Plot')
    plot.legend(handles = handler)
    plot.savefig(comb_dir + 'Nx_Plot.jpg')
    plot.cla()
    plot.close()

#Plots the cumulative length
def CumulativePlot(contigSizes, output_dir, comb_dir, labels):
    handler = []
    for i in xrange(len(contigSizes)):
        prevContig = 0
    
        cumulContig = [0] * len(contigSizes[i])
        for j in xrange(len(contigSizes[i])):
            prevContig += contigSizes[i][j]
            cumulContig[j] = prevContig/1000
        
        plot.figure(1)
        
        plot.plot(cumulContig, linewidth = 2.0)
        plot.ylabel('Contig Length (kbp)')
        plot.xlabel('Contig Index')
        plot.grid(b = True)
        plot.title('Cumulative Length')
        plot.savefig(output_dir[i]+'CumulPlot.jpg')
        plot.cla()
        plot.close()
        
        plot.figure(2)
        fig = plot.plot(cumulContig, linewidth = 2.0)
        handler.append(patch.Patch(color=fig[-1].get_color(), label=labels[i]))
    
    plot.ylabel('Contig Length (kbp)')
    plot.xlabel('Contig Index')
    plot.grid(b = True)
    plot.title('Cumulative Length')
    plot.legend(handles = handler)
    plot.savefig(comb_dir+'CumulPlot.jpg')
    plot.cla()
    plot.close()

#Plots a frequency graph of GC content
def GcPlot(gcCont, numWindows, output_dir, comb_dir, labels):
    handler = []
    for i in xrange(len(gcCont)):
        gcHist = [0] * 100
        for gc in xrange(int(numWindows[i])):
            gcHist[int(100*gcCont[i][gc]) - 1] += 1
        
        plot.figure(1)
        plot.plot(gcHist, linewidth = 0.5)
        plot.ylabel('# Windows')
        plot.xlabel('GC (%)')
        plot.grid(b = True)
        plot.title('GC content')
        plot.savefig(output_dir[i]+'GCPlot.jpg')
        plot.cla()
        plot.close()
        
        plot.figure(2)
        fig = plot.plot(gcHist, linewidth = 0.5)
        
        handler.append(patch.Patch(color=fig[-1].get_color(), label=labels[i]))
    
    plot.ylabel('# Windows')
    plot.xlabel('GC (%)')
    plot.grid(b = True)
    plot.title('GC content')
    plot.legend(handles = handler)
    plot.savefig(comb_dir+'GCPlot.jpg')
    plot.cla()
    plot.close()
 
def Visualize():
    arguments = sys.argv[1::]
    numArgs = len(arguments) - 1
    statFiles = arguments[:(numArgs)/2]
    directories = arguments[(numArgs)/2:numArgs:]
    combDir = arguments[numArgs]
    labels = []
    
    for directory in directories:
        if 'idba' in directory.lower():
            labels.append('IDBA')
        if 'megahit' in directory.lower():
            labels.append('MEGAHIT')
        if 'spades' in directory.lower():
            labels.append('SPADES')
    
    statistics, contigSizes, numWindows, gcCont, nxArray = openStatFile(statFiles)
    Create_table(statistics, directories, combDir, labels)
    Nx_plot(nxArray, directories, combDir, labels)
    CumulativePlot(contigSizes, directories, combDir, labels)
    GcPlot(gcCont, numWindows, directories, combDir, labels)
    
Visualize()